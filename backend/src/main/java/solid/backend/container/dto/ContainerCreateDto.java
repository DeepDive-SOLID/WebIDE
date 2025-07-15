package solid.backend.container.dto;

import lombok.*;
import solid.backend.common.enums.ContainerVisibility;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

/**
 * 컨테이너 생성 요청 DTO
 * 새로운 컨테이너 생성 시 필요한 정보를 담는 객체
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContainerCreateDto {
    
    @NotBlank(message = "컨테이너 이름은 필수입니다")
    @Size(max = 20, message = "컨테이너 이름은 20자를 초과할 수 없습니다")
    private String containerName; // 컨테이너 이름
    
    @Size(max = 200, message = "컨테이너 설명은 200자를 초과할 수 없습니다")
    private String containerContent; // 컨테이너 설명 (선택사항)
    
    @NotNull(message = "공개 여부는 필수입니다")
    private ContainerVisibility visibility; // 컨테이너 공개 범위 (PUBLIC/PRIVATE)
    
    @Builder.Default
    private List<String> invitedMemberIds = new ArrayList<>(); // 초대할 멤버 ID 목록 (선택사항)
}