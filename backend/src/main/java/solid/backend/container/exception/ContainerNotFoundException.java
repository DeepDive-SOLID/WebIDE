package solid.backend.container.exception;

/**
 * 컨테이너를 찾을 수 없을 때 발생하는 예외
 */
public class ContainerNotFoundException extends RuntimeException {
    
    /**
     * 메시지를 포함한 예외 생성
     * @param message 예외 메시지
     */
    public ContainerNotFoundException(String message) {
        super(message);
    }
    
    /**
     * 컨테이너 ID를 포함한 예외 생성
     * @param containerId 찾을 수 없는 컨테이너 ID
     */
    public ContainerNotFoundException(Integer containerId) {
        super("컨테이너를 찾을 수 없습니다. ID: " + containerId);
    }
}