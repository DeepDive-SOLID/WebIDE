package solid.backend.websocket;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionUnsubscribeEvent;
import solid.backend.chat.service.ChatService;

/**
 * 사용자가 WebSocket 연결을 끊거나 구독 해제(퇴장 처리, 채팅방 나가기) 할 때 이벤트로 호출됨
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class WebSocketHandler {

    private final ChatService chatService;

    /**
     * 설명 : 사용자의 WebSocket 연결이 끊어졌을 때 호출됨
     * @param event 웹소켓 세션의 연결이 끊기면 발생하는 이벤트
     */
    @EventListener
    public void handleSessionDisconnect(SessionDisconnectEvent event) {
        SimpMessageHeaderAccessor accessor = SimpMessageHeaderAccessor.wrap(event.getMessage());
        // 세션에서 사용자 ID 추출
        String memberId = String.valueOf(getMemberIdFromHeaderAccessor(accessor));

        // 연결이 끊긴 사용자 퇴장 처리
        chatService.disconnect(memberId);
    }

    /**
     * 설명 : 사용자가 구독을 해제했을 때 호출됨
     * @param event 웹소켓 세션의 연결이 끊기면 발생하는 이벤트
     */
    @EventListener
    public void handleSessionUnsubscribe(SessionUnsubscribeEvent event) {
        SimpMessageHeaderAccessor accessor = SimpMessageHeaderAccessor.wrap(event.getMessage());
        // 세션에서 사용자 ID 추출
        String memberId = String.valueOf(getMemberIdFromHeaderAccessor(accessor));
        // 구독 해제한 채팅방 ID 추출
        Integer chatRoomId = getChatRoomIdFromHeaderAccessor(accessor);

        // 채팅방에서 퇴장 처리
        chatService.unsubscribe(memberId, chatRoomId);
    }

    /**
     * 설명 : 세션에서 사용자 ID 추출
     * @param accessor 세션 정보를 담고 있는 메시지 헤더
     * @return String
     */
    private String getMemberIdFromHeaderAccessor(SimpMessageHeaderAccessor accessor) {
        if (accessor.getSessionAttributes() == null || !accessor.getSessionAttributes().containsKey("memberId")) {
            throw new IllegalStateException("No SessionAttributes or memberId. sessionId: " + accessor.getSessionId());
        }

        return (String) accessor.getSessionAttributes().get("memberId");
    }

    /**
     * 설명 : 구독 해제한 채팅방 ID 추출
     * @param accessor 세션 정보를 담고 있는 메시지 헤더
     * @return Long
     */
    private Integer getChatRoomIdFromHeaderAccessor(SimpMessageHeaderAccessor accessor) {
        if (accessor.getDestination() == null || !accessor.getDestination().startsWith("/topic/chatRooms/")) {
            throw new IllegalStateException("No destination or destination is not start with '/topic/chatRooms/'. destination: " + accessor.getDestination());
        }

        // 예: /topic/chat-rooms/123 → 123 추출
        return Integer.parseInt(accessor.getDestination().replace("/topic/chatRooms/", ""));
    }
}
