package solid.backend.chat.message.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;
import solid.backend.chat.message.service.MessageService;

@Controller
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    @MessageMapping("/chat-rooms/{chatRoomId}")
    public void sendChatMessage(String content, SimpMessageHeaderAccessor accessor, @DestinationVariable("chatRoomId") Long chatRoomId) {
        if (accessor.getSessionAttributes() == null || !accessor.getSessionAttributes().containsKey("memberId")) {
            throw new IllegalStateException("No SessionAttributes or memberId. sessionId: " + accessor.getSessionId());
        }

        String memberId = String.valueOf((Long) accessor.getSessionAttributes().get("memberId"));

        messageService.sendChatMessage(memberId, chatRoomId, content);
    }

}
