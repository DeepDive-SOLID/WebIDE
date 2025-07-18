package solid.backend.Directory.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import solid.backend.Directory.dto.DirectoryDelDto;
import solid.backend.Directory.dto.DirectoryDto;
import solid.backend.Directory.dto.DirectoryListDto;
import solid.backend.Directory.dto.DirectoryUpdDto;
import solid.backend.common.FileManager;
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
    public List<DirectoryDto> getDirectoryList(DirectoryListDto directoryListDto) {
        List<Directory> directories = directoryRepository.findAllByContainer_ContainerId(directoryListDto.getContainerId());

        return directories.stream()
                .map(d -> new DirectoryDto(
                        d.getDirectoryId(),
                        d.getTeam().getTeamId(),
                        d.getContainer().getContainerId(),
                        d.getDirectoryName(),
                        d.getDirectoryRoot()
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
        String rootPath = "/";

        if (directoryDto.getDirectoryRoot() != null && !directoryDto.getDirectoryRoot().equals("/")) {
            String parentName = directoryDto.getDirectoryRoot().replace("/", "").trim();

            Optional<Directory> parentOpt = directoryRepository
                    .findByDirectoryNameAndContainer_ContainerIdAndTeam_TeamId(
                            parentName,
                            directoryDto.getContainerId(),
                            directoryDto.getTeamId()
                    );

            if (parentOpt.isEmpty()) {
                throw new IllegalArgumentException("상위 디렉터리 '" + parentName + "'를 찾을 수 없습니다.");
            }

            Directory parent = parentOpt.get();
            rootPath = parent.getDirectoryRoot() + "/" + parent.getDirectoryName();
        }

        Directory directory = new Directory();
        directory.setDirectoryName(directoryDto.getDirectoryName());
        directory.setDirectoryRoot(rootPath);
        directory.setContainer(containerRepository.findById(directoryDto.getContainerId()).orElseThrow());
        directory.setTeam(teamRepository.findById(directoryDto.getTeamId()).orElseThrow());

        Directory saved = directoryRepository.save(directory);

        String relativePath = (rootPath + "/" + directoryDto.getDirectoryName()).replaceAll("/+", "/").replaceFirst("^/", "");
        fileManager.createDirectoryPath(directoryDto.getContainerId(), relativePath);

        directoryDto.setDirectoryId(saved.getDirectoryId());
        directoryDto.setDirectoryRoot(saved.getDirectoryRoot());
        return directoryDto;
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

        System.out.println(oldName + " -> " + newName);

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
        if( directoryDelDto.getContainerId() == null ) throw new IllegalArgumentException("삭제할 디렉터리가 없습니다.");
        directoryRepository.deleteById(directoryDelDto.getDirectoryId());
        fileManager.deleteDirectory(directoryDelDto.getContainerId(), directoryDelDto.getDirectoryRoot(), directoryDelDto.getDirectoryName());
    }
}
