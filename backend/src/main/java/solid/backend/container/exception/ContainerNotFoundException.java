package solid.backend.container.exception;

/**
 * 컨테이너를 찾을 수 없을 때 발생하는 예외
 */
public class ContainerNotFoundException extends RuntimeException {
    
    public ContainerNotFoundException(String message) {
        super(message);
    }
    
    public ContainerNotFoundException(Long containerId) {
        super("컨테이너를 찾을 수 없습니다. ID: " + containerId);
    }
}