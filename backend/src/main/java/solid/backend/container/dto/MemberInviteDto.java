package solid.backend.container.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import solid.backend.common.enums.Authority;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 멤버 초대 요청 DTO
 * 컨테이너에 새로운 멤버를 초대할 때 사용
 * ROOT 권한을 가진 사용자만 멤버 초대 가능
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberInviteDto {
    
    @NotBlank(message = "회원 ID는 필수입니다")
    private String memberId; // 초대할 회원 ID
    
    @NotNull(message = "권한은 필수입니다")
    private Authority authority; // 부여할 권한 (ROOT/USER)
}