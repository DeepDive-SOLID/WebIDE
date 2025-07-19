package solid.backend.container.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 컨테이너 통계 정보 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContainerStatisticsDto {
    
    /** 컨테이너 고유 식별자 */
    private Long containerId;
    /** 컨테이너 이름 */
    private String containerName;
    /** 전체 멤버 수 */
    private Long totalMemberCount;
    /** 활성 멤버 수 (최근 30일 내 활동) */
    private Long activeMemberCount;
    /** 비활성 멤버 수 (30일 이상 미활동) */
    private Long inactiveMemberCount;
    /** 가장 최근 멤버 활동 시간 */
    private LocalDateTime lastActivityDate;
    /** 컨테이너 생성일시 */
    private LocalDateTime createdDate;
    
    /** ROOT 권한을 가진 멤버 수 */
    private Long rootMemberCount;
    /** USER 권한을 가진 멤버 수 */
    private Long userMemberCount;
    
    /**
     * 활동률 계산 (백분율)
     * null 검사를 포함하여 안전하게 계산
     */
    public Double getActivityRate() {
        if (totalMemberCount == null || totalMemberCount == 0 || activeMemberCount == null) {
            return 0.0;
        }
        return (activeMemberCount.doubleValue() / totalMemberCount.doubleValue()) * 100;
    }
}