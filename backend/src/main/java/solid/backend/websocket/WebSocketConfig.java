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
 * 앱 초기 설정 시 작동, Spring WebSocket 에 클래스들을 등록
 */
@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final JwtUtil jwtTokenProvider;
    private final MemberRepository memberRepository;

    /**
     * 설명 : STOMP 엔드포인트를 설정
     * @param registry STOMP 엔드포인트를 등록하기 위한 객체
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")                                            // 연결 주소
                .setAllowedOrigins(
                        "http://15.164.250.218",
                        "http://localhost:5173",
                        "http://localhost:5174")                                        // CORS 허용 출처
                .withSockJS();                                                        // SockJS 지원
    }

    /**
     * 설명 : 메시지 브로커 설정
     * @param registry STOMP 엔드포인트를 등록하기 위한 객체
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // 클라이언트가 구독할 주소(prefix): /topic
        registry.enableSimpleBroker("/topic");
        // 클라이언트가 메시지를 보낼 주소(prefix): /app
        registry.setApplicationDestinationPrefixes("/app");
    }

    /**
     * 설명 : STOMP 메시지를 가로채는 인터셉터 등록
     * @param registration 클라이언트 인바운드 채널에 인터셉터를 추가하기 위한 설정 객체
     */
    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        // STOMP 메시지를 가로채서 JWT 인증 수행
        registration.interceptors(new MyChannelInterceptor(jwtTokenProvider, memberRepository));
    }
}
