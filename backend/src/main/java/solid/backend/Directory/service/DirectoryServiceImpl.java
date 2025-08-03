package solid.backend.Directory.service;

import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import solid.backend.Directory.dto.DirectoryDelDto;
import solid.backend.Directory.dto.DirectoryDto;
import solid.backend.Directory.dto.DirectoryListDto;
import solid.backend.Directory.dto.DirectoryUpdDto;
import solid.backend.common.FileManager;
import solid.backend.common.DirectoryPathUtils;
import solid.backend.entity.Container;
import solid.backend.entity.Directory;
import solid.backend.jpaRepository.ContainerRepository;
import solid.backend.jpaRepository.DirectoryRepository;
import solid.backend.jpaRepository.TeamRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DirectoryServiceImpl implements DirectoryService {

    private final DirectoryRepository directoryRepository;
    private final ContainerRepository containerRepository;
    private final TeamRepository teamRepository;
    private final FileManager fileManager;

    /**
     * 설명: 컨테이너 전체 디렉터리 조회
     * @param directoryListDto
     * @return List<DirectoryDto>
     */
    @Override
    @Transactional(readOnly = true)
    public List<DirectoryDto> getDirectoryList(DirectoryListDto directoryListDto) {
        List<Directory> directories = directoryRepository.findAllByContainer_ContainerId(directoryListDto.getContainerId());

        return directories.stream()
                .map(d -> new DirectoryDto(
                        d.getDirectoryId(),
                        d.getContainer().getContainerId(),
                        d.getTeam().getTeamId(),
                        d.getDirectoryName(),
                        d.getDirectoryRoot(),
                        !d.getQuestions().isEmpty()  // hasQuestion: Question이 하나라도 있으면 true
                ))
                .collect(Collectors.toList());
    }

    /**
     * 설명: 디렉터리 생성
     * @param directoryDto
     * @return DirectoryDto
     */
    @Override
    @Transactional
    public DirectoryDto createDirectory(DirectoryDto directoryDto) {
        String rootPath = DirectoryPathUtils.normalizePath(directoryDto.getDirectoryRoot());
        
        // 부모 디렉터리 존재 여부 확인
        Container container = containerRepository.findById(directoryDto.getContainerId())
                .orElseThrow(() -> new IllegalArgumentException("컨테이너를 찾을 수 없습니다."));
        
        if (!"/".equals(rootPath)) {
            validateParentDirectory(rootPath, container);
        }

        Directory directory = new Directory();
        directory.setDirectoryName(directoryDto.getDirectoryName());
        directory.setDirectoryRoot(rootPath);
        directory.setContainer(container);
        directory.setTeam(teamRepository.findById(directoryDto.getTeamId()).orElseThrow());

        Directory saved = directoryRepository.save(directory);

        String fullPath = DirectoryPathUtils.buildFullPath(rootPath, directoryDto.getDirectoryName());
        String relativePath = fullPath.replaceFirst("^/", "");
        fileManager.createDirectoryPath(directoryDto.getContainerId(), relativePath);

        return new DirectoryDto(
                saved.getDirectoryId(),
                saved.getContainer().getContainerId(),
                saved.getTeam().getTeamId(),
                saved.getDirectoryName(),
                saved.getDirectoryRoot(),
                false  // hasQuestion: 새로 생성된 디렉터리는 아직 문제가 없음
        );
    }

    /**
     * 설명: 디렉터리 이름 변경
     * @param directoryUpdDto
     */
    @Override
    @Transactional
    public void updateDirectory(DirectoryUpdDto directoryUpdDto) {
        Directory directory = directoryRepository.findById(directoryUpdDto.getDirectoryId())
                .orElseThrow(() -> new IllegalArgumentException("해당 디렉터리가 존재하지 않습니다."));

        String oldName = directory.getDirectoryName();
        String newName = directoryUpdDto.getDirectoryName();
        String oldPath = DirectoryPathUtils.buildFullPath(directory.getDirectoryRoot(), oldName);
        String newPath = DirectoryPathUtils.buildFullPath(directory.getDirectoryRoot(), newName);

        // 자식 디렉터리들의 경로 업데이트
        List<Directory> allDirectories = directoryRepository
                .findAllByContainer_ContainerId(directory.getContainer().getContainerId());
        
        for (Directory child : allDirectories) {
            if (DirectoryPathUtils.isChildPath(child.getDirectoryRoot(), oldPath)) {
                String updatedRoot = child.getDirectoryRoot().replace(oldPath, newPath);
                child.setDirectoryRoot(updatedRoot);
                directoryRepository.save(child);
            }
        }

        // 파일 시스템에서 디렉터리명 변경
        fileManager.renameDirectory(
                directory.getContainer().getContainerId(),
                directory.getDirectoryRoot(),
                oldName,
                newName
        );

        // DB 업데이트
        directory.setDirectoryName(newName);
        directoryRepository.save(directory);
    }


    /**
     * 설명: 디렉터리 삭제
     * @param directoryDelDto
     */
    @Override
    @Transactional
    public void deleteDirectory(DirectoryDelDto directoryDelDto) {
        if (directoryDelDto.getContainerId() == null) {
            throw new IllegalArgumentException("컨테이너 ID가 필요합니다.");
        }
        
        Directory directory = directoryRepository.findById(directoryDelDto.getDirectoryId())
                .orElseThrow(() -> new IllegalArgumentException("삭제할 디렉터리가 없습니다."));
        
        String pathToDelete = DirectoryPathUtils.buildFullPath(directory.getDirectoryRoot(), directory.getDirectoryName());
        
        // 모든 자식 디렉터리 찾기
        List<Directory> allDirectories = directoryRepository
                .findAllByContainer_ContainerId(directoryDelDto.getContainerId());
        
        List<Directory> toDelete = allDirectories.stream()
                .filter(d -> d.getDirectoryId().equals(directoryDelDto.getDirectoryId()) ||
                            DirectoryPathUtils.isChildPath(d.getDirectoryRoot(), pathToDelete))
                .collect(Collectors.toList());
        
        // 파일 시스템에서 삭제 (하위 디렉터리부터 삭제)
        toDelete.stream()
                .sorted((d1, d2) -> Integer.compare(
                        DirectoryPathUtils.calculateDepth(d2.getDirectoryRoot()),
                        DirectoryPathUtils.calculateDepth(d1.getDirectoryRoot())
                ))
                .forEach(d -> fileManager.deleteDirectory(
                        d.getContainer().getContainerId(),
                        d.getDirectoryRoot(),
                        d.getDirectoryName()
                ));
        
        // DB에서 삭제
        directoryRepository.deleteAll(toDelete);
    }
    
    /**
     * 특정 디렉터리의 하위 디렉터리 조회
     */
    @Override
    @Transactional(readOnly = true)
    public List<DirectoryDto> getChildDirectories(Integer directoryId) {
        Directory directory = directoryRepository.findById(directoryId)
                .orElseThrow(() -> new IllegalArgumentException("디렉터리를 찾을 수 없습니다."));
        
        String parentPath = DirectoryPathUtils.buildFullPath(directory.getDirectoryRoot(), directory.getDirectoryName());
        
        List<Directory> children = directoryRepository
                .findChildrenByPath(directory.getContainer().getContainerId(), parentPath + "/");
        
        return children.stream()
                .filter(d -> d.getDirectoryRoot().equals(parentPath)) // 직접 자식만 필터링
                .map(d -> new DirectoryDto(
                        d.getDirectoryId(),
                        d.getContainer().getContainerId(),
                        d.getTeam().getTeamId(),
                        d.getDirectoryName(),
                        d.getDirectoryRoot(),
                        !d.getQuestions().isEmpty()
                ))
                .collect(Collectors.toList());
    }
    
    /**
     * 루트 디렉터리만 조회
     */
    @Override
    @Transactional(readOnly = true)
    public List<DirectoryDto> getRootDirectories(Integer containerId) {
        List<Directory> roots = directoryRepository.findRootDirectories(containerId);
        
        return roots.stream()
                .map(d -> new DirectoryDto(
                        d.getDirectoryId(),
                        d.getContainer().getContainerId(),
                        d.getTeam().getTeamId(),
                        d.getDirectoryName(),
                        d.getDirectoryRoot(),
                        !d.getQuestions().isEmpty()
                ))
                .collect(Collectors.toList());
    }
    
    /**
     * 부모 디렉터리 존재 여부 검증
     */
    private void validateParentDirectory(String directoryRoot, Container container) {
        String parentName = DirectoryPathUtils.extractParentName(directoryRoot);
        String parentPath = DirectoryPathUtils.extractParentPath(directoryRoot);
        
        if (parentName != null) {
            Optional<Directory> parent = directoryRepository
                    .findByDirectoryRootAndDirectoryNameAndContainer(parentPath, parentName, container);
            
            if (parent.isEmpty()) {
                throw new IllegalArgumentException("상위 디렉터리 '" + parentName + "'를 찾을 수 없습니다.");
            }
        }
    }
}
