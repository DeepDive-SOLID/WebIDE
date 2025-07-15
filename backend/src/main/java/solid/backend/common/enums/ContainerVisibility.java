package solid.backend.common.enums;

import lombok.Getter;

/**
 * 컨테이너 공개 범위 Enum
 * PUBLIC: 모든 사용자가 조회 가능
 * PRIVATE: 컨테이너 멤버만 접근 가능
 */
@Getter
public enum ContainerVisibility {
    PUBLIC(true), // 공개 컨테이너
    PRIVATE(false); // 비공개 컨테이너
    
    private final boolean isPublic; // 공개 여부 플래그
    
    ContainerVisibility(boolean isPublic) {
        this.isPublic = isPublic;
    }
}