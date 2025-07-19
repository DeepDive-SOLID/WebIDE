package solid.backend.container.constant;

/**
 * 컨테이너 모듈 상수 정의
 */
public final class ContainerConstants {
    
    private ContainerConstants() {
        // 인스턴스 생성 방지
    }
    
    /** 권한 타입 - 소유자 */
    public static final String AUTHORITY_ROOT = "ROOT";
    
    /** 권한 타입 - 일반 멤버 */
    public static final String AUTHORITY_USER = "USER";
    
    /** 비활동 기준 개월수 (6개월) */
    public static final int INACTIVE_MONTHS = 6;
    
    /** 컨테이너 이름 최소 길이 */
    public static final int CONTAINER_NAME_MIN_LENGTH = 1;
    
    /** 컨테이너 이름 최대 길이 */
    public static final int CONTAINER_NAME_MAX_LENGTH = 20;
    
    /** 컨테이너 설명 최대 길이 */
    public static final int CONTAINER_CONTENT_MAX_LENGTH = 200;
    
    /** 일괄 작업 최대 개수 */
    public static final int BATCH_OPERATION_MAX_SIZE = 100;
    
    /** 스케줄러 실행 시간 (매일 새벽 2시) */
    public static final String INACTIVE_MEMBER_CLEANUP_SCHEDULE = "0 0 2 * * *";
    
    // 에러 메시지 상수
    /** 컨테이너를 찾을 수 없을 때 */
    public static final String ERROR_CONTAINER_NOT_FOUND = "컨테이너를 찾을 수 없습니다: ";
    
    /** 회원을 찾을 수 없을 때 */
    public static final String ERROR_MEMBER_NOT_FOUND = "회원을 찾을 수 없습니다: ";
    
    /** 초대할 회원을 찾을 수 없을 때 */
    public static final String ERROR_INVITED_MEMBER_NOT_FOUND = "초대할 회원을 찾을 수 없습니다: ";
    
    /** ROOT 권한이 필요할 때 */
    public static final String ERROR_ROOT_AUTHORITY_REQUIRED = "ROOT 권한이 필요합니다.";
    
    /** 소유자 제거 불가 */
    public static final String ERROR_OWNER_CANNOT_BE_REMOVED = "소유자는 제거할 수 없습니다.";
    
    /** 소유자 탈퇴 불가 */
    public static final String ERROR_OWNER_CANNOT_LEAVE = "소유자는 컨테이너를 탈퇴할 수 없습니다.";
    
    /** 사용자 ID 필수 */
    public static final String ERROR_MEMBER_ID_REQUIRED = "사용자 ID는 필수입니다";
    
    /** 비공개 컨테이너 접근 불가 */
    public static final String ERROR_PRIVATE_CONTAINER_ACCESS_DENIED = "비공개 컨테이너에 접근할 수 없습니다";
    
    /** 접근 권한 없음 */
    public static final String ERROR_NO_ACCESS_PERMISSION = "접근 권한이 없습니다.";
    
    /** 컨테이너 멤버가 아님 */
    public static final String ERROR_NOT_CONTAINER_MEMBER = "컨테이너 멤버가 아닙니다.";
}