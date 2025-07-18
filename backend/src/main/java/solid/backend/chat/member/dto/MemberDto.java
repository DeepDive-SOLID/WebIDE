package solid.backend.chat.member.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import solid.backend.entity.Member;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MemberDto {

    private String memberId;
    private String memberName;
    private LocalDateTime createdAt;
    private LocalDateTime lastModifiedAt;

    public MemberDto(Member member) {
        this.memberId = member.getMemberId();
        this.memberName = member.getMemberName();
        this.createdAt = LocalDateTime.now();
        this.lastModifiedAt = LocalDateTime.now();
    }
}
