package solid.backend.docker.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 코드 실행 요청 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CodeExecutionRequestDto {
    
    /** 프로그래밍 언어 */
    @NotBlank(message = "언어는 필수입니다")
    @Pattern(regexp = "^(python|java|javascript|cpp|c)$", 
             message = "지원하지 않는 언어입니다")
    private String language;
    
    /** 실행할 코드 */
    @NotBlank(message = "코드는 필수입니다")
    private String code;
    
    /** 표준 입력 (선택) */
    private String input;
    
    /** 컨테이너 ID */
    @NotNull(message = "컨테이너 ID는 필수입니다")
    private Long containerId;
    
    /** 파일 경로 (선택) */
    private String filePath;
}