package solid.backend.chat.member.dto;

import lombok.Data;
import solid.backend.entity.Member;

import java.util.List;

@Data
public class MembersDto {

    private List<MemberDto> members;

    public MembersDto(List<Member> members) {
        this.members = members.stream()
                .map(MemberDto::new)
                .toList();
    }

}
