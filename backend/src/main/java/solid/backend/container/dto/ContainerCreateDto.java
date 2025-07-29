package solid.backend.container.dto;

import lombok.*;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;
import static solid.backend.container.constant.ContainerConstants.*;

/**
 * 컨테이너 생성 요청 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContainerCreateDto {
    
    /** 컨테이너 이름 (1~20자, 문자/숫자/공백/하이픈/언더스코어) */
    @NotBlank(message = "컨테이너 이름은 필수입니다")
    @Size(min = CONTAINER_NAME_MIN_LENGTH, max = CONTAINER_NAME_MAX_LENGTH, message = "컨테이너 이름은 " + CONTAINER_NAME_MIN_LENGTH + "~" + CONTAINER_NAME_MAX_LENGTH + "자여야 합니다")
    @Pattern(regexp = "^[a-zA-Z0-9가-힣\\s\\-_]+$", message = "컨테이너 이름은 문자, 숫자, 공백, 하이픈, 언더스코어만 사용 가능합니다")
    private String containerName;
    
    /** 컨테이너 설명 (선택, 최대 200자) */
    @Size(max = CONTAINER_CONTENT_MAX_LENGTH, message = "컨테이너 설명은 " + CONTAINER_CONTENT_MAX_LENGTH + "자를 초과할 수 없습니다")
    private String containerContent;
    
    /** 컨테이너 공개 여부 (true: 공개, false: 비공개) */
    @NotNull(message = "공개 여부는 필수입니다")
    private Boolean isPublic;
    
    /** 초대할 멤버 ID 목록 (기본값: 빈 리스트) */
    @NotNull(message = "초대할 멤버 목록은 null일 수 없습니다")
    @Builder.Default
    private List<String> invitedMemberIds = new ArrayList<>();
}