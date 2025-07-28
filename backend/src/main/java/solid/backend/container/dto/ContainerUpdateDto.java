package solid.backend.container.dto;

import lombok.*;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import solid.backend.container.constant.ContainerConstants;

/**
 * 컨테이너 수정 요청 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContainerUpdateDto {
    
    /** 변경할 컨테이너 이름 (선택, 최대 20자) */
    @Size(max = ContainerConstants.CONTAINER_NAME_MAX_LENGTH, message = "컨테이너 이름은 " + ContainerConstants.CONTAINER_NAME_MAX_LENGTH + "자를 초과할 수 없습니다")
    @Pattern(regexp = "^[a-zA-Z0-9가-힣\\s\\-_]+$", message = "컨테이너 이름은 문자, 숫자, 공백, 하이픈, 언더스코어만 사용 가능합니다")
    private String containerName;
    
    /** 변경할 컨테이너 설명 (선택, 최대 200자) */
    @Size(max = ContainerConstants.CONTAINER_CONTENT_MAX_LENGTH, message = "컨테이너 설명은 " + ContainerConstants.CONTAINER_CONTENT_MAX_LENGTH + "자를 초과할 수 없습니다")
    private String containerContent;
    
    /** 변경할 공개 여부 (선택, true: 공개, false: 비공개) */
    private Boolean isPublic;
}