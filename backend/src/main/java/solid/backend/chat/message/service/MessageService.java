package solid.backend.chat.message.service;

import solid.backend.entity.Container;
import solid.backend.entity.Member;
import solid.backend.entity.Message;

public interface MessageService {

    void sendChatMessage(String memberId, Long chatRoomId, String content);

    void sendSystemMessage(String memberId, Long chatRoomId, String content);

    void publish(Message message);

    void sendJoinedMessage(Member member, Container container);

    void sendLeftMessage(Member member, Container container);
}
