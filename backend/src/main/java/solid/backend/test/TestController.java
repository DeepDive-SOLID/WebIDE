package solid.backend.test;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import solid.backend.Jwt.AccessToken;
import solid.backend.Jwt.JwtUtil;
import solid.backend.common.ApiResponse;

import java.util.HashMap;
import java.util.Map;

/**
 * 테스트용 컨트롤러
 * 개발 환경에서만 사용
 */
@RestController
@RequestMapping("/test")
@RequiredArgsConstructor
public class TestController {
    
    private final JwtUtil jwtUtil;
    
    /**
     * 테스트용 JWT 토큰 생성
     * @param memberId 회원 ID
     * @return JWT 토큰
     */
    @GetMapping("/token/{memberId}")
    public ApiResponse<Map<String, String>> getTestToken(@PathVariable String memberId) {
        // AccessToken 객체 생성
        AccessToken tokenInfo = AccessToken.builder()
                .memberId(memberId)
                .build();
        
        String accessToken = jwtUtil.createAccessToken(tokenInfo);
        String refreshToken = jwtUtil.createRefreshToken(tokenInfo);
        
        Map<String, String> tokens = new HashMap<>();
        tokens.put("accessToken", accessToken);
        tokens.put("refreshToken", refreshToken);
        tokens.put("memberId", memberId);
        
        return ApiResponse.success(tokens, "테스트 토큰 생성 성공");
    }
}