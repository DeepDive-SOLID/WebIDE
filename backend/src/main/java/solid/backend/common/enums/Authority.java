package solid.backend.common.enums;

import lombok.Getter;

/**
 * 컨테이너 멤버 권한 Enum
 * ROOT: 관리자 권한 (멤버 초대/제거, 권한 변경 가능)
 * USER: 일반 사용자 권한 (읽기/쓰기만 가능)
 */
@Getter
public enum Authority {
    ROOT("ROOT", "관리자"), // 컨테이너 관리자 권한
    USER("USER", "사용자"); // 일반 사용자 권한
    
    private final String code; // 권한 코드
    private final String displayName; // 화면 표시명
    
    Authority(String code, String displayName) {
        this.code = code;
        this.displayName = displayName;
    }
}