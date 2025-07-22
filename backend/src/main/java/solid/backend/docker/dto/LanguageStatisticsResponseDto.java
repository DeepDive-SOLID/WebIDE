package solid.backend.docker.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 언어별 통계 응답 DTO
 * 
 * 컨테이너의 언어별 실행 통계 전체 응답을 담는 DTO입니다.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LanguageStatisticsResponseDto {
    
    /**
     * 컨테이너 ID
     */
    private Long containerId;
    
    /**
     * 언어별 통계 목록
     */
    private List<LanguageStatisticsDto> languageStatistics;
    
    /**
     * 총 실행 횟수
     */
    private Long totalExecutions;
    
    /**
     * 통계 생성 시간
     */
    @Builder.Default
    private LocalDateTime generatedAt = LocalDateTime.now();
}