package solid.backend.docker.exception;

/**
 * 도커 실행 관련 예외
 */
public class DockerExecutionException extends RuntimeException {
    
    public DockerExecutionException(String message) {
        super(message);
    }
    
    public DockerExecutionException(String message, Throwable cause) {
        super(message, cause);
    }
}