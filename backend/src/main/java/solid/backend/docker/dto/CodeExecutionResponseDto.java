package solid.backend.docker.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 코드 실행 결과 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CodeExecutionResponseDto {
    
    /** 실행 ID */
    private Long executionId;
    /** 프로그래밍 언어 */
    private String language;
    /** 실행한 코드 */
    private String code;
    /** 표준 입력 */
    private String input;
    /** 표준 출력 */
    private String output;
    /** 에러 출력 */
    private String errorOutput;
    /** 실행 상태 */
    private String status;
    /** 실행 시간 (밀리초) */
    private Long executionTime;
    /** 메모리 사용량 (바이트) */
    private Long memoryUsed;
    /** 생성 일시 */
    private LocalDateTime createdAt;
    /** 완료 일시 */
    private LocalDateTime completedAt;
    
    /**
     * 메모리 사용량을 MB 단위로 반환
     * @return MB 단위 메모리 사용량
     */
    public Double getMemoryUsedInMB() {
        return memoryUsed != null ? memoryUsed / (1024.0 * 1024.0) : null;
    }
    
    /**
     * 실행 시간을 초 단위로 반환
     * @return 초 단위 실행 시간
     */
    public Double getExecutionTimeInSeconds() {
        return executionTime != null ? executionTime / 1000.0 : null;
    }
}