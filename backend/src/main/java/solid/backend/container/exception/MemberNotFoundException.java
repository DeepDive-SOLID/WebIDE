package solid.backend.container.exception;

/**
 * 멤버를 찾을 수 없을 때 발생하는 예외
 */
public class MemberNotFoundException extends RuntimeException {
    public MemberNotFoundException(String message) {
        super(message);
    }
}