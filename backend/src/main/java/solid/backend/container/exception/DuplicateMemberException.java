package solid.backend.container.exception;

/**
 * 이미 컨테이너에 속한 멤버를 중복 초대할 때 발생하는 예외
 */
public class DuplicateMemberException extends RuntimeException {
    
    public DuplicateMemberException(String message) {
        super(message);
    }
    
    public DuplicateMemberException(String memberId, Integer containerId) {
        super(String.format("회원 %s는 이미 컨테이너 %d에 속해있습니다", memberId, containerId));
    }
}