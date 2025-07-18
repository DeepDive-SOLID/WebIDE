package solid.backend.chat.message.service;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import solid.backend.chat.message.dto.MessageDto;
import solid.backend.entity.Container;
import solid.backend.entity.Member;
import solid.backend.entity.Message;
import solid.backend.jpaRepository.ContainerRepository;
import solid.backend.jpaRepository.MemberRepository;
import solid.backend.jpaRepository.MessageRepository;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

    public static final String CHAT_DESTINATION_PREFIX = "/topic/chat-rooms/";
    public static final String HAS_JOINED = "님이 참가하였습니다.";
    public static final String HAS_LEFT = "님이 나갔습니다.";

    private final MemberRepository memberRepository;
    private final ContainerRepository containerRepository;
    private final MessageRepository messageRepository;
    private final SimpMessagingTemplate simpMessagingTemplate;

    @Override
    @Transactional
    public void sendChatMessage(String memberId, Long chatRoomId, String content) {
        Member member = memberRepository.findById(memberId).orElseThrow();
        Container container = containerRepository.findById(chatRoomId).orElseThrow();

        Message chatMessage = Message.createChatMessage(member, container, content);

        publish(chatMessage);
    }

    @Override
    @Transactional
    public void sendSystemMessage(String memberId, Long chatRoomId, String content) {
        Member member = memberRepository.findById(memberId).orElseThrow();
        Container container = containerRepository.findById(chatRoomId).orElseThrow();

        Message systemMessage = Message.createSystemMessage(member, container, content);

        publish(systemMessage);
    }

    @Override
    @Transactional
    public void publish(Message message) {
        simpMessagingTemplate.convertAndSend(CHAT_DESTINATION_PREFIX + message.getContainer().getContainerId(), new MessageDto(message));
        messageRepository.save(message);
    }

    @Override
    @Transactional
    public void sendJoinedMessage(Member member, Container container) {
        Message joinMessage = Message.createSystemMessage(member, container, member.getMemberName() + HAS_JOINED);
        publish(joinMessage);
    }

    @Override
    @Transactional
    public void sendLeftMessage(Member member, Container container) {
        Message leftMessage = Message.createSystemMessage(member, container, member.getMemberName() + HAS_LEFT);
        publish(leftMessage);
    }
}
