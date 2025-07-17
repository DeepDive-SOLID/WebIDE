package solid.backend.websocket;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import solid.backend.Jwt.JwtUtil;
import solid.backend.jpaRepository.MemberRepository;

/**
 * 앱 초기 설정 시 작동
 * 위 클래스들을 Spring WebSocket에 등록하는 클래스
 */
@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final JwtUtil jwtTokenProvider;
    private final MemberRepository memberRepository;

    /**
     * 클라이언트가 WebSocket 서버에 연결할 수 있도록 STOMP 엔드포인트를 설정
     * 예: 프론트엔드에서 SockJS('/ws')로 연결
     * 서버와 웹소켓 연결을 위한 엔드포인트, 클라이언트에서 sockJS 객체를 만들 때 사용
     * @param registry
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws") // 연결 주소
                .setAllowedOrigins("http://localhost:5173") // CORS 허용 출처
                .withSockJS(); // SockJS 지원
    }

    /**
     * 메시지 브로커를 설정합니다.
     * - 클라이언트가 구독할 주소(prefix): /topic
     * - 클라이언트가 메시지를 보낼 주소(prefix): /app
     * 예: 클라이언트 → "/app/chat.send" → 서버 처리 후 "/topic/chat.roomId"로 브로드캐스트
     * @param registry
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // 구독 주소
        // 브로커에서 처리할 주소
        registry.enableSimpleBroker("/topic");
        // 전송 주소
        // 컨트롤러가 처리할 주소
        // 메시지를 주고받는 주소
        // 클라이언트가 "/app/chat"으로 메시지를 보낸다면, 해당 메시지는 브로커로 가기 전에 저희가 작성한 ChatController의 @MessageMapping("/chat") 어노테이션이 붙어있는 메서드로 먼저 들어옴
        registry.setApplicationDestinationPrefixes("/app");
    }

    /**
     * 클라이언트로부터 들어오는 STOMP 메시지를 가로채는 인터셉터를 등록합니다.
     * CONNECT 요청 시 JWT 토큰을 검사하고, 사용자 인증 정보를 세션에 저장합니다.
     * @param registration
     */
    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        // 메시지 인터셉터 등록
        registration.interceptors(new MyChannelInterceptor(jwtTokenProvider, memberRepository));
    }
}
