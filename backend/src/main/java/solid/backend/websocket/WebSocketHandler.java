package solid.backend.websocket;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionUnsubscribeEvent;
import solid.backend.chat.member.service.MemberService;
import solid.backend.container.service.ContainerService;

/**
 * 사용자가 WebSocket 연결을 끊거나 구독 해제할 때 이벤트로 호출됨
 * 퇴장 처리, 채팅방 나가기 등 처리
 * 연결 종료 및 구독 해제 시 처리
 * DISCONNECT, UNSUBSCRIBE 이벤트 발생 시 작동
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class WebSocketHandler {

    private final MemberService memberService;
    private final ContainerService containerService;

    /**
     * STOMP 서브 프로토콜을 사용하는 웹소켓 세션의 연결이 끊기면 발생하는 이벤트 {@link SessionDisconnectEvent}를 처리합니다.
     */

    /**
     * 설명 : 사용자의 WebSocket 연결이 끊겼을 때 실행됨
     * @param event
     */
    @EventListener
    public void handleSessionDisconnect(SessionDisconnectEvent event) {
        SimpMessageHeaderAccessor accessor = SimpMessageHeaderAccessor.wrap(event.getMessage());
        String memberId = String.valueOf(getMemberIdFromHeaderAccessor(accessor));

        // 연결이 끊긴 사용자 퇴장 처리
        memberService.leave(memberId);
    }

    /**
     * 설명 : 사용자가 채팅방 구독을 해제했을 때 실행됨
     * @param event
     */
    @EventListener
    public void handleSessionUnsubscribe(SessionUnsubscribeEvent event) {
        SimpMessageHeaderAccessor accessor = SimpMessageHeaderAccessor.wrap(event.getMessage());
        String memberId = String.valueOf(getMemberIdFromHeaderAccessor(accessor));
        Long chatRoomId = getChatRoomIdFromHeaderAccessor(accessor);

        // 채팅방에서 퇴장 처리
        containerService.leave(memberId, chatRoomId);
    }

    /**
     * 설명 : 세션에서 사용자 ID 추출
     * @param accessor
     * @return
     */
    private Long getMemberIdFromHeaderAccessor(SimpMessageHeaderAccessor accessor) {
        if (accessor.getSessionAttributes() == null || !accessor.getSessionAttributes().containsKey("memberId")) {
            throw new IllegalStateException("No SessionAttributes or memberId. sessionId: " + accessor.getSessionId());
        }

        return (Long) accessor.getSessionAttributes().get("memberId");
    }

    /**
     * 설명 : 구독 해제한 채팅방 ID 추출
     * @param accessor
     * @return
     */
    private Long getChatRoomIdFromHeaderAccessor(SimpMessageHeaderAccessor accessor) {
        if (accessor.getDestination() == null || !accessor.getDestination().startsWith("/topic/chat-rooms/")) {
            throw new IllegalStateException("No destination or destination is not start with '/topic/chat-rooms/'. destination: " + accessor.getDestination());
        }

        // 예: /topic/chat-rooms/123 → 123 추출
        return Long.parseLong(accessor.getDestination().replace("/topic/chat-rooms/", ""));
    }
}
