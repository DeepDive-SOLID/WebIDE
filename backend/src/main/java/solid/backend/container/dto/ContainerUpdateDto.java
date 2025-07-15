package solid.backend.container.dto;

import lombok.*;
import solid.backend.common.enums.ContainerVisibility;

import jakarta.validation.constraints.Size;

/**
 * 컨테이너 수정 요청 DTO
 * 컨테이너 정보 수정 시 필요한 정보를 담는 객체
 * 모든 필드는 선택사항이며, 전달된 필드만 수정됨
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContainerUpdateDto {
    
    @Size(max = 20, message = "컨테이너 이름은 20자를 초과할 수 없습니다")
    private String containerName; // 컨테이너 이름 (선택사항)
    
    @Size(max = 200, message = "컨테이너 설명은 200자를 초과할 수 없습니다")
    private String containerContent; // 컨테이너 설명 (선택사항)
    
    private ContainerVisibility visibility; // 공개 범위 (선택사항)
}