package solid.backend.container.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import solid.backend.entity.TeamUser;
import solid.backend.common.enums.Authority;

import java.time.LocalDateTime;

/**
 * 팀 멤버 응답 DTO
 * 컨테이너에 속한 멤버 정보를 반환할 때 사용
 * 멤버의 기본 정보와 권한, 활동 시간 정보 포함
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GroupMemberResponseDto {
    
    private Long teamUserId; // 팀 멤버 ID
    private String memberId; // 회원 ID
    private String memberName; // 회원 이름
    private String memberEmail; // 회원 이메일
    private Authority authority; // 멤버 권한 (ROOT/USER)
    private LocalDateTime joinedDate; // 팀 가입 날짜
    private LocalDateTime lastActivityDate; // 마지막 활동 날짜
    
    /**
     * TeamUser 엔티티를 DTO로 변환
     * @param teamUser 팀 멤버 엔티티
     * @return GroupMemberResponseDto
     */
    public static GroupMemberResponseDto from(TeamUser teamUser) {
        return GroupMemberResponseDto.builder()
                .teamUserId(Long.valueOf(teamUser.getTeamUserId()))
                .memberId(teamUser.getMember().getMemberId())
                .memberName(teamUser.getMember().getMemberName())
                .memberEmail(teamUser.getMember().getMemberEmail())
                .authority(Authority.valueOf(teamUser.getTeamAuth().getAuthId()))
                .joinedDate(teamUser.getJoinedDate())
                .lastActivityDate(teamUser.getLastActivityDate())
                .build();
    }
}