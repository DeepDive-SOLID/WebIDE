package solid.backend.websocket;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import solid.backend.Jwt.JwtUtil;
import solid.backend.jpaRepository.MemberRepository;

/**
 * STOMP 메시지를 가로채서 JWT 인증 수행
 * 클라이언트가 WebSocket으로 연결 요청할 때 실행됨
 * WebSocket 연결 시 JWT 토큰 검증
 * CONNECT 요청 시 작동
 */
@RequiredArgsConstructor
public class MyChannelInterceptor implements ChannelInterceptor {

    private final JwtUtil jwtTokenProvider;
    private final MemberRepository memberRepository;

    /**
     * 설명 : STOMP 메시지가 채널로 들어오기 전 가로챔
     * @param message
     * @param channel
     * @return
     */
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        // STOMP 헤더 추출
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        // STOMP 명령 종류 추출 (CONNECT, SUBSCRIBE 등)
        StompCommand command = accessor.getCommand();

        // 클라이언트가 웹소켓 연결할 때만 처리
        if (StompCommand.CONNECT.equals(command)) {
            handleConnect(accessor);
        }

        // 나머지는 그대로 진행
        return message;
    }

    /**
     * 설명 : CONNECT 요청 시 JWT 인증 처리
     * @param accessor
     */
    private void handleConnect(StompHeaderAccessor accessor) {
        if (accessor == null || accessor.getSessionAttributes() == null) {
            throw new IllegalStateException("No StompHeaderAccessor");
        }

        // 클라이언트가 보낸 accessToken 추출
        String accessToken = accessor.getFirstNativeHeader("accessToken");
        if (accessToken == null || accessToken.isBlank()) {
            throw new IllegalStateException("No accessToken");
        }

        // accessToken 유효성 검사
        if (!jwtTokenProvider.validateToken(accessToken)) {
            throw new IllegalStateException("Invalid accessToken");
        }

        // 토큰에서 사용자 ID 추출
        String memberId = jwtTokenProvider.getMemberId(accessToken);

        if (!memberRepository.existsById(memberId)) {
            throw new IllegalStateException("Invalid memberId");
        }

        // 세션에 사용자 정보 저장 (이후 핸들러에서 꺼내 쓸 수 있음)
        accessor.getSessionAttributes().put("memberId", memberId);
    }
}
