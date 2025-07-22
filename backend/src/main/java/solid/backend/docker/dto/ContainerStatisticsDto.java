package solid.backend.docker.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 컨테이너 실행 통계 DTO
 * 
 * 컨테이너의 전체 실행 통계 정보를 담는 DTO입니다.
 * API 엔드포인트: GET /api/docker/containers/{containerId}/statistics
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContainerStatisticsDto {
    
    /**
     * 컨테이너 ID
     */
    private Long containerId;
    
    /**
     * 총 실행 횟수
     */
    private Long totalExecutions;
    
    /**
     * 성공한 실행 횟수
     */
    private Long successfulExecutions;
    
    /**
     * 실패한 실행 횟수
     */
    private Long failedExecutions;
    
    /**
     * 평균 실행 시간 (밀리초)
     */
    private Double averageExecutionTime;
    
    /**
     * 최대 실행 시간 (밀리초)
     */
    private Long maxExecutionTime;
    
    /**
     * 최소 실행 시간 (밀리초)
     */
    private Long minExecutionTime;
    
    /**
     * 평균 메모리 사용량 (바이트)
     */
    private Double averageMemoryUsage;
    
    /**
     * 최대 메모리 사용량 (바이트)
     */
    private Long maxMemoryUsage;
    
    /**
     * 통계 기간 시작일
     */
    private LocalDateTime periodStart;
    
    /**
     * 통계 기간 종료일
     */
    private LocalDateTime periodEnd;
    
    /**
     * 통계 생성 시간
     */
    @Builder.Default
    private LocalDateTime generatedAt = LocalDateTime.now();
}