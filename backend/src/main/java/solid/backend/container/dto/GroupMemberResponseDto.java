package solid.backend.container.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import solid.backend.entity.TeamUser;

import java.time.LocalDateTime;

/**
 * 팀 멤버 응답 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GroupMemberResponseDto {
    
    /** 팀 멤버 고유 식별자 */
    private Long teamUserId;
    /** 회원 ID */
    private String memberId;
    /** 회원 이름 */
    private String memberName;
    /** 회원 이메일 */
    private String memberEmail;
    /** 멤버 권한 (ROOT/USER) */
    private String authority;
    /** 컨테이너 가입일시 */
    private LocalDateTime joinedDate;
    /** 마지막 활동일시 */
    private LocalDateTime lastActivityDate;
    
    /**
     * TeamUser 엔티티를 DTO로 변환
     * @param teamUser 팀 멤버 엔티티
     * @return GroupMemberResponseDto
     */
    public static GroupMemberResponseDto from(TeamUser teamUser) {
        if (teamUser == null) {
            throw new IllegalArgumentException("TeamUser cannot be null");
        }
        if (teamUser.getMember() == null) {
            throw new IllegalArgumentException("TeamUser must have a member");
        }
        if (teamUser.getTeamAuth() == null) {
            throw new IllegalArgumentException("TeamUser must have an authority");
        }
        if (teamUser.getTeamUserId() == null) {
            throw new IllegalArgumentException("TeamUser must have a teamUserId");
        }
        
        return GroupMemberResponseDto.builder()
                .teamUserId(teamUser.getTeamUserId().longValue())
                .memberId(teamUser.getMember().getMemberId())
                .memberName(teamUser.getMember().getMemberName())
                .memberEmail(teamUser.getMember().getMemberEmail())
                .authority(teamUser.getTeamAuth() != null ? teamUser.getTeamAuth().getAuthId() : null)
                .joinedDate(teamUser.getJoinedDate())
                .lastActivityDate(teamUser.getLastActivityDate())
                .build();
    }
}