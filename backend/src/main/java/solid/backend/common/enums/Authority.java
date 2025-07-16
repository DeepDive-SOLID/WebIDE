package solid.backend.common.enums;

import lombok.Getter;

/**
 * 컨테이너 멤버 권한 Enum
 * ROOT/USER: 기존 권한 시스템 (하위 호환성)
 * ADMIN/INVITE/WRITE/READ: 세분화된 권한 시스템
 */
@Getter
public enum Authority {
    ROOT("ROOT", "관리자"),       // 기존 최고 권한 (하위 호환성)
    ADMIN("ADMIN", "관리자"),     // 모든 권한
    INVITE("INVITE", "초대권한"), // 초대 + 수정 + 읽기
    WRITE("WRITE", "수정권한"),   // 수정 + 읽기
    USER("USER", "사용자"),       // 기존 일반 권한 (하위 호환성)
    READ("READ", "읽기권한");     // 읽기만
    
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
        return this == ROOT || this == ADMIN || this == INVITE;
    }
    
    /**
     * 컨테이너 수정 가능 여부
     */
    public boolean canWrite() {
        return this != READ && this != USER;
    }
    
    /**
     * 컨테이너 설정 변경 가능 여부
     */
    public boolean canManage() {
        return this == ROOT || this == ADMIN;
    }
    
    /**
     * ROOT 권한인지 확인 (하위 호환성)
     */
    public boolean isRoot() {
        return this == ROOT || this == ADMIN;
    }
    
    /**
     * USER 권한인지 확인 (하위 호환성)
     */
    public boolean isUser() {
        return this == USER || this == READ;
    }
    
    /**
     * 세분화된 권한으로 변환 (기존 ROOT/USER를 새 권한으로 매핑)
     */
    public Authority toDetailedAuthority() {
        if (this == ROOT) return ADMIN;
        if (this == USER) return WRITE;
        return this;
    }
    
    /**
     * 기존 권한으로 변환 (새 권한을 ROOT/USER로 매핑)
     */
    public Authority toLegacyAuthority() {
        if (this == ADMIN || this == INVITE) return ROOT;
        if (this == WRITE || this == READ) return USER;
        return this;
    }
}