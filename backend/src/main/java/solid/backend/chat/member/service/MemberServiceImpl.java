package solid.backend.chat.member.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import solid.backend.chat.member.dto.MemberDto;
import solid.backend.chat.member.dto.MembersDto;
import solid.backend.chat.message.service.MessageService;
import solid.backend.entity.Container;
import solid.backend.entity.Member;
import solid.backend.jpaRepository.ContainerRepository;
import solid.backend.jpaRepository.MemberChatRoomRepository;
import solid.backend.jpaRepository.MemberRepository;

import java.awt.*;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final MemberChatRoomRepository memberChatRoomRepository;
    private final ContainerRepository containerRepository;
    private final MessageService messageService;

    @Override
    @Transactional
    public void leave(String userId) {
        Member member = memberRepository.findById(userId).orElseThrow();

        member.getMemberChatRooms().forEach(memberChatRoom -> {
            Container container = memberChatRoom.getContainer();
            messageService.sendLeftMessage(member, container);
        });

        memberChatRoomRepository.deleteAllByMember(member);
//        containerRepository.deleteAllEmptyChatRooms();

//        memberRepository.delete(member);
    }

    @Override
    public ResponseEntity<MembersDto> findAll() {
        return ResponseEntity.ok(new MembersDto(memberRepository.findAll()));
    }

    @Override
    public ResponseEntity<MemberDto> findById(String memberId) {
        return ResponseEntity.ok(new MemberDto(memberRepository.findById(memberId).orElseThrow()));
    }
}
