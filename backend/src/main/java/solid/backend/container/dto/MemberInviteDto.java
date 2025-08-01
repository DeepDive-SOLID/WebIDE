package solid.backend.container.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * 멤버 초대 요청 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberInviteDto {
    
    @NotBlank(message = "회원 ID는 필수입니다")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "회원 ID는 영문, 숫자, 언더스코어만 사용 가능합니다")
    @Size(min = 3, max = 20, message = "회원 ID는 3~20자여야 합니다")
    private String memberId; // 초대할 회원 ID
}