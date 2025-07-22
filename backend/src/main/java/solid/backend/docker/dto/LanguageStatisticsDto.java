package solid.backend.docker.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 언어별 실행 통계 DTO
 * 
 * 특정 프로그래밍 언어의 실행 통계 정보를 담는 DTO입니다.
 * API 엔드포인트: GET /api/docker/containers/{containerId}/statistics/languages
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LanguageStatisticsDto {
    
    /**
     * 프로그래밍 언어
     */
    private String language;
    
    /**
     * 해당 언어의 총 실행 횟수
     */
    private Long executionCount;
    
    /**
     * 전체 실행 대비 비율 (%)
     */
    private Double percentage;
    
    /**
     * 성공한 실행 횟수
     */
    private Long successCount;
    
    /**
     * 실패한 실행 횟수
     */
    private Long failureCount;
    
    /**
     * 평균 실행 시간 (밀리초)
     */
    private Double averageExecutionTime;
    
    /**
     * 평균 메모리 사용량 (바이트)
     */
    private Double averageMemoryUsage;
    
    /**
     * 가장 최근 실행 시간
     */
    private String lastExecutedAt;
}