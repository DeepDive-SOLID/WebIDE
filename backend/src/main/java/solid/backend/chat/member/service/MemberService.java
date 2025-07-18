package solid.backend.chat.member.service;

import org.springframework.http.ResponseEntity;
import solid.backend.chat.member.dto.MemberDto;
import solid.backend.chat.member.dto.MembersDto;

public interface MemberService {

    void leave(String memberId);

    ResponseEntity<MembersDto> findAll();

    ResponseEntity<MemberDto> findById(String memberId);
}
