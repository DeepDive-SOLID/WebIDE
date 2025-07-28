package solid.backend.container.exception;

/**
 * 유효하지 않은 멤버 ID가 제공될 때 발생하는 예외
 */
public class InvalidMemberException extends RuntimeException {
    public InvalidMemberException(String message) {
        super(message);
    }
}