package solid.backend.container.dto;

import lombok.*;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

/**
 * 컨테이너 일괄 공개 상태 변경 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BatchContainerVisibilityDto {
    
    @NotEmpty(message = "컨테이너 ID 목록은 필수입니다")
    @Size(max = 100, message = "한 번에 최대 100개까지 변경 가능합니다")
    private List<Long> containerIds; // 변경할 컨테이너 ID 목록
    
    @NotNull(message = "공개 상태는 필수입니다")
    private Boolean isPublic; // 변경할 공개 상태 (true: 공개, false: 비공개)
}