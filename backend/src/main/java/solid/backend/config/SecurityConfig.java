package solid.backend.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import solid.backend.Jwt.JwtFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtFilter jwtFiler;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // 회원가입, 로그인 허용
                        .requestMatchers("/sign/**").permitAll()
                        .requestMatchers("/token/refresh").permitAll()

                        // 컨테이너 접근
                        .requestMatchers("/api/containers/**").hasRole("USER")
                        .requestMatchers("/directory/**").hasRole("USER")
                        .requestMatchers("/docker/**").hasRole("USER")
                        .requestMatchers("/api/progress/**").hasRole("USER")
                        .requestMatchers("/question/**").hasRole("USER")
                        .requestMatchers("/CodeFile/**").hasRole("USER")

                        // 채팅창 접근
                        .requestMatchers("/chatRooms/**").hasRole("USER")
                        .requestMatchers("/api/chatRooms/**").hasRole("USER")
                        .requestMatchers("/topic").hasRole("USER")
                        .requestMatchers("/app").hasRole("USER")
                        .requestMatchers("/ws").hasRole("USER")

                        // 마이페이지 접근
                        .requestMatchers("/mypage/member/**").hasRole("USER")
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtFiler, UsernamePasswordAuthenticationFilter.class)
                .formLogin(form -> form.disable());

        return http.build();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}