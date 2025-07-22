package solid.backend.CodeFile.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import solid.backend.CodeFile.dto.CodeFileDelDto;
import solid.backend.CodeFile.dto.CodeFileListDto;
import solid.backend.CodeFile.dto.CodeFileSaveDto;
import solid.backend.CodeFile.dto.CodeFileUpdDto;
import solid.backend.config.FileStorageConfig;
import solid.backend.entity.CodeFile;
import solid.backend.entity.Directory;
import solid.backend.jpaRepository.CodeFileRepository;
import solid.backend.jpaRepository.DirectoryRepository;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CodeFileServiceImpl implements CodeFileService {

    private final CodeFileRepository codeFileRepository;
    private final DirectoryRepository directoryRepository;
    private final FileStorageConfig fileStorageConfig;

    /**
     * 설명: 코드 파일 전체 조회
     * @return ist<CodeFileListDto>
     */
    @Override
    @Transactional
    public List<CodeFileListDto> getCodeFileList() {
        return codeFileRepository.findAll().stream()
                .map(codeFile -> new CodeFileListDto (
                        codeFile.getCodeFileId(),
                        codeFile.getDirectory().getDirectoryId(),
                        codeFile.getCodeFilePath(),
                        codeFile.getCodeFileName(),
                        codeFile.getCodeFileUploadDt(),
                        codeFile.getCodeFileCreateDt()
                )).collect(Collectors.toList());
    }

    /**
     * 설명: 코드 파일 내용 조회
     * @param codeFileId
     * @return String
     */
    @Override
    @Transactional
    public String codeContent(Integer codeFileId) {
        CodeFile codeFile = codeFileRepository.findById(String.valueOf(codeFileId))
                .orElseThrow(() -> new IllegalArgumentException("파일이 존재하지 않습니다."));

        Path filePath = Paths.get(codeFile.getCodeFilePath());

        try {
            return Files.readString(filePath);
        } catch (IOException e) {
            throw new RuntimeException("파일 내용 읽기 실패" + e.getMessage(), e);
        }
    }

    /**
     * 설명: 코드 파일 생성
     * @param codeFileSaveDto
     */
    @Override
    @Transactional
    public void createCodeFile(CodeFileSaveDto codeFileSaveDto) {
        Directory directory = directoryRepository.findById(codeFileSaveDto.getDirectoryId())
                .orElseThrow(() -> new IllegalArgumentException("디렉터리가 존재하지 않습니다."));

        String containerPart = "container-" + directory.getContainer().getContainerId();
        String fullPath = Paths.get(
                fileStorageConfig.getUploadDir(),
                containerPart,
                directory.getDirectoryRoot(),
                directory.getDirectoryName()
        ).toString();

        File targetDir = new File(fullPath);
        if (!targetDir.exists()) {
            throw new RuntimeException("지정된 디렉터리 경로가 존재하지 않습니다: " + fullPath);
        }

        String filePath = Paths.get(fullPath, codeFileSaveDto.getCodeFileName()).toString();

        try {
            Files.writeString(Path.of(filePath), codeFileSaveDto.getCodeContent(),
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("코드 파일 저장 실패: " + e.getMessage(), e);
        }

        CodeFile entity = new CodeFile();

        entity.setDirectory(directory);
        entity.setCodeFilePath(filePath);
        entity.setCodeFileName(codeFileSaveDto.getCodeFileName());
        entity.setCodeFileUploadDt(LocalDate.now());
        entity.setCodeFileCreateDt(LocalDate.now());

        codeFileRepository.save(entity);
    }

    /**
     * 설명: 코드 파일 내용 변경
     * @param codeFileUpdDto
     */
    @Override
    @Transactional
    public void updateCodeFile(CodeFileUpdDto codeFileUpdDto) {
        CodeFile codeFile = codeFileRepository.findById(String.valueOf(codeFileUpdDto.getCodeFileId()))
                .orElseThrow(() -> new IllegalArgumentException("파일이 존재하지 않습니다."));

        Path filePath = Paths.get(codeFile.getCodeFilePath());

        try {
            Files.writeString(filePath, codeFileUpdDto.getCodeContent(), StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("파일 수정 중 오류 발생: " + e.getMessage(), e);
        }

        codeFile.setCodeFileUploadDt(LocalDate.now());
    }

    /**
     * 설명: 코드 파일 삭제
     * @param codeFileDelDto
     */
    @Override
    @Transactional
    public void deleteCodeFile(CodeFileDelDto codeFileDelDto) {
        CodeFile codeFile = codeFileRepository.findById(String.valueOf(codeFileDelDto.getCodeFileId()))
                .orElseThrow(() -> new IllegalArgumentException("삭제할 코드 파일이 존재하지 않습니다."));

        Path filePath = Paths.get(codeFile.getCodeFilePath());

        try {
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            throw new RuntimeException("파일 삭제 중 오류 발생: " + e.getMessage());
        }

        codeFileRepository.delete(codeFile);
    }
}
