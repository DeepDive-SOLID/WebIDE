package solid.backend.container.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 컨테이너 통계 정보 DTO
 * 컨테이너의 멤버 수, 활동 상태 등의 통계 정보를 반환
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContainerStatisticsDto {
    
    private Long containerId;
    private String containerName;
    private Long totalMemberCount;        // 전체 멤버 수
    private Long activeMemberCount;       // 최근 30일 내 활동한 멤버 수
    private Long inactiveMemberCount;     // 비활동 멤버 수
    private LocalDateTime lastActivityDate; // 가장 최근 활동 시간
    private LocalDateTime createdDate;     // 컨테이너 생성일
    
    // 권한별 멤버 수
    private Long rootMemberCount;
    private Long userMemberCount;
    
    /**
     * 활동률 계산 (백분율)
     */
    public Double getActivityRate() {
        if (totalMemberCount == 0) return 0.0;
        return (activeMemberCount.doubleValue() / totalMemberCount.doubleValue()) * 100;
    }
}