package solid.backend.container.exception;

/**
 * 컨테이너 접근 권한이 없을 때 발생하는 예외
 */
public class UnauthorizedContainerAccessException extends RuntimeException {
    
    public UnauthorizedContainerAccessException(String message) {
        super(message);
    }
    
    public UnauthorizedContainerAccessException() {
        super("컨테이너에 대한 접근 권한이 없습니다");
    }
}