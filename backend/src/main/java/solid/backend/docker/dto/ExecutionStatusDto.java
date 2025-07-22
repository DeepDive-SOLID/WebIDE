package solid.backend.docker.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 실행 상태 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExecutionStatusDto {
    
    /** 실행 ID */
    private Long executionId;
    /** 실행 상태 */
    private String status;
    /** 상태 메시지 */
    private String message;
    /** 진행률 (0-100) */
    private Integer progress;
}