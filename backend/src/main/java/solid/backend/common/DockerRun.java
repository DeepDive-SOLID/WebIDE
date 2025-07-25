package solid.backend.common;

import org.springframework.stereotype.Component;
import solid.backend.Docker.dto.DockerResultDto;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
public class DockerRun {

    /**
     * 설명: 언어별 도커 컨테이너 생성 명령어 추출
     * @param filePath
     * @param extension
     * @return String[]
     */
    public String[] buildDockerCommand(String filePath, String extension, String input, int mem) {
        Path path = Path.of(filePath);
        String dir = path.getParent().toString();
        String fileName = path.getFileName().toString();
        String memoryLimit = mem + "m";

        return switch (extension) {
            case "py" -> new String[]{
                    "docker", "run", "--memory=" + memoryLimit, "--rm", "-v", dir + ":/app", "-i",
                    "python:3.11", "sh", "-c",
                    "echo \"" + input + "\" | python /app/" + fileName
            };
            case "java" -> new String[]{
                    "docker", "run", "--memory=" + memoryLimit, "--rm", "-v", dir + ":/app", "-i",
                    "openjdk:21", "sh", "-c",
                    "cd /app && javac " + fileName + " && echo \"" + input + "\" | java " + fileName.replace(".java", "")
            };
            case "js" -> new String[]{
                    "docker", "run", "--memory=" + memoryLimit, "--rm", "-v", dir + ":/app", "-i",
                    "node:20", "sh", "-c",
                    "echo \"" + input + "\" | node /app/" + fileName
            };
            default -> throw new UnsupportedOperationException("지원하지 않는 언어입니다: " + extension);
        };
    }

    /**
     * 설명: 생성된 명령어를 사용하여 도커 컨테이너 실행
     * @param command
     * @return String
     */
    public DockerResultDto runDockerCommand(String[] command, float timeoutSeconds) {
        try {
            long startTime = System.nanoTime();
            // 명령어 가져와서 도커 실행
            ProcessBuilder pb = new ProcessBuilder(command);
            pb.redirectErrorStream(true);
            Process process = pb.start();

            long timeoutMillis = (long) (timeoutSeconds * 1000);

            boolean finished = process.waitFor(timeoutMillis, TimeUnit.MILLISECONDS);
            long endTime = System.nanoTime();
            float elapsedTime = (endTime - startTime) / 1_000_000_000.0f;

            if (!finished) {
                process.destroyForcibly(); // 타임아웃이면 강제 종료
                return new DockerResultDto("Time Out", elapsedTime);
            }

            String output; //실행 결과
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                output = reader.lines().collect(Collectors.joining("\n"));
            }

            // 결과 출력 조건 처리
            if (output.contains("error:") || output.contains("Exception")) {
                return new DockerResultDto(output, elapsedTime);
            }

            return new DockerResultDto(output, elapsedTime);
        } catch (IOException e) {
            throw new RuntimeException("도커 실행 실패: " + e.getMessage(), e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 설명: 코드 파일 확장자 추출
     * @param filePath
     * @return String
     */
    public String getFileExtension(String filePath) {
        int dotIndex = filePath.lastIndexOf(".");
        return (dotIndex == -1) ? "" : filePath.substring(dotIndex + 1).toLowerCase();
    }

    /**
     * 설명: 확장자를 통해 사용언어 출력 데이터 변환
     * @param extension
     * @return String
     */
    public String getLanguageByExtension(String extension) {
        return switch (extension) {
            case "py" -> "python";
            case "java" -> "java";
            case "js" -> "javascript";
            default -> "unknown";
        };
    }
}
