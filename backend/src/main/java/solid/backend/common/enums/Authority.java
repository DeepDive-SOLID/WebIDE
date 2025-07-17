package solid.backend.common.enums;

import lombok.Getter;

/**
 * 컨테이너 멤버 권한 Enum
 * ROOT: 컨테이너 소유자 권한
 * USER: 일반 사용자 권한
 */
@Getter
public enum Authority {
    ROOT("ROOT", "관리자"),  // 컨테이너 소유자 및 관리자 권한
    USER("USER", "사용자");  // 일반 사용자 권한
    
    private final String code; // 권한 코드
    private final String displayName; // 화면 표시명
    
    Authority(String code, String displayName) {
        this.code = code;
        this.displayName = displayName;
    }
    
    /**
     * 권한 계층 확인
     * @param required 필요한 권한
     * @return 현재 권한이 필요한 권한 이상인지 여부
     */
    public boolean hasPermission(Authority required) {
        return this.ordinal() <= required.ordinal();
    }
    
    /**
     * 멤버 초대 가능 여부
     */
    public boolean canInvite() {
        return this == ROOT;
    }
    
    /**
     * 컨테이너 수정 가능 여부
     */
    public boolean canWrite() {
        return true;  // ROOT와 USER 모두 수정 가능
    }
    
    /**
     * 컨테이너 설정 변경 가능 여부
     */
    public boolean canManage() {
        return this == ROOT;
    }
    
    /**
     * ROOT 권한인지 확인
     */
    public boolean isRoot() {
        return this == ROOT;
    }
    
    /**
     * USER 권한인지 확인
     */
    public boolean isUser() {
        return this == USER;
    }
}