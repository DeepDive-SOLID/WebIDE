package solid.backend.common;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import solid.backend.config.FileStorageConfig;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class FileManager {

    private final FileStorageConfig fileStorageConfig;

    /**
     * 설명 : 파일 업로드 공통 메소드
     * @param file, subFolder
     * @return Stirng
     */
    public String addFile(MultipartFile file, String subFolder) {
        try {
            if (file == null || file.isEmpty()) return null;

            // 파일 크기 제한 (1MB)
            long maxFileSize = 1024 * 1024;
            if (file.getSize() > maxFileSize) {
                throw new IllegalArgumentException("파일 크기는 1MB를 초과할 수 없습니다.");
            }

            // 파일 확장자 검사
            String originalName = file.getOriginalFilename();
            if (originalName == null) {
                throw new IllegalArgumentException("파일명이 없습니다.");
            }

            String lowerName = originalName.toLowerCase();
            if (!lowerName.endsWith(".jpg") && !lowerName.endsWith(".png")) {
                throw new IllegalArgumentException("허용되지 않는 파일 형식입니다. (jpg, png만 가능)");
            }

            // 파일명 길이 제한
            if (originalName.length() > 300) {
                throw new IllegalArgumentException("파일명이 너무 깁니다. 300자 이하로 제한됩니다.");
            }

            // 저장 경로 설정
            String uploadDir = fileStorageConfig.getUploadDir() + File.separator + subFolder;
            File dir = new File(uploadDir);

            // 디렉토리가 없으면 생성
            if (!dir.exists()) dir.mkdirs();

            // 한글 및 특수문자 제거
            String safeName = originalName.replaceAll("[^a-zA-Z0-9._-]", "_");
            String fileName = UUID.randomUUID() + "_" + safeName;

            // 실제 저장
            String savePath = uploadDir + File.separator + fileName;
            file.transferTo(new File(savePath));

            // DB에 저장할 경로 (URL)
            return "/" + subFolder + "/" + fileName;

        } catch (IOException | IllegalStateException e) {
            throw new RuntimeException("파일 업로드 실패: " + e.getMessage(), e);
        } catch (IllegalArgumentException e) {
            throw e;
        }
    }

    /**
     * 설명 : 파일 삭제 공통 메서드
     * @param fileImgUrl
     */
    public void deleteFile(String fileImgUrl) {
        if (fileImgUrl == null || fileImgUrl.isEmpty()) return;

        String baseDir = fileStorageConfig.getUploadDir();
        String fullPath = baseDir + File.separator + fileImgUrl;

        File file = new File(fullPath);
        log.info("삭제 대상 경로: " + fullPath);
        if (file.exists()) {
            boolean deleted = file.delete();
            if (!deleted) log.error("파일 삭제 실패: " + fullPath);
        } else {
            log.error("파일이 존재하지 않음: " + fullPath);
        }
    }

    /**
     * 설명 : 파일 조회 url 가공
     * @param fileImgUrl
     * @return String
     */
    public String getFileUrl(String fileImgUrl) {
        if (fileImgUrl == null || fileImgUrl.isEmpty()) return null;
        String processedUrl = ServletUriComponentsBuilder.fromHttpUrl("http://15.164.250.218")
                .path("/solid")
                .toUriString();
        return processedUrl + fileImgUrl;
    }

    /**
     * 설명: 디렉터리 생성
     * @param containerId
     * @param directoryName
     */
    public void createDirectoryPath(Integer containerId, String directoryName) {
        String directoryPath = String.format("container-%d/%s", containerId, directoryName);
        Path fullPath = Paths.get(fileStorageConfig.getUploadDir(), directoryPath)
                .toAbsolutePath()
                .normalize();

        try {
            Files.createDirectories(fullPath);
        } catch (IOException e) {
            throw new RuntimeException("디렉터리 생성 실패: " + fullPath, e);
        }
    }

    /**
     * 설명: 디렉터리 메서드
     * @param containerId
     * @param directoryRoot
     * @param directoryName
     */
    public void deleteDirectory(Integer containerId, String directoryRoot, String directoryName) {
        if (directoryRoot == null || directoryRoot.isEmpty()) return;

        String baseDir = fileStorageConfig.getUploadDir();
        String fullPath = baseDir + File.separator + String.format("container-%d%s/%s", containerId, directoryRoot, directoryName);

        File file = new File(fullPath);
        log.info("삭제 대상 경로: " + fullPath);
        if (file.exists()) {
            boolean deleted = deleteRecursively(file);
            if (!deleted) log.error("파일 삭제 실패: " + fullPath);
        } else {
            log.error("파일이 존재하지 않음: " + fullPath);
        }
    }

    /**
     * 설명: 디렉터리 이름 변경 메서드
     * @param containerId
     * @param directoryRoot
     * @param oldName
     * @param newName
     */
    public void renameDirectory(Integer containerId, String directoryRoot, String oldName, String newName) {
        String baseDir = fileStorageConfig.getUploadDir();
        String oldPathStr = baseDir + File.separator + String.format("container-%d%s/%s", containerId, directoryRoot, oldName);
        String newPathStr = baseDir + File.separator + String.format("container-%d%s/%s", containerId, directoryRoot, newName);

        File oldDir = new File(oldPathStr);
        File newDir = new File(newPathStr);

        if (!oldDir.exists()) {
            throw new RuntimeException("기존 디렉터리가 존재하지 않습니다: " + oldPathStr);
        }

        boolean success = oldDir.renameTo(newDir);
        if (!success) {
            throw new RuntimeException("디렉터리 이름 변경 실패: " + oldPathStr + " → " + newPathStr);
        }
    }
    /**
     * 설명: 하위 디렉터리 삭제
     * @param file
     * @return file.delete()
     */
    private boolean deleteRecursively(File file) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null) {
                for (File child : files) {
                    if (!deleteRecursively(child)) return false;
                }
            }
        }
        return file.delete();
    }
}
