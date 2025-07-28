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
                        // 테스트를 위한 permitAll 처리
                        .requestMatchers("/**").permitAll()
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
