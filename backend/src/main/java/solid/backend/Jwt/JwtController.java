package solid.backend.Jwt;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/token")
public class JwtController {

    private final JwtUtil jwtUtil;

    /**
     * 설명: refresh token을 활용하여 access token 재발급
     * @param request
     * @return ResponseEntity
     */
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshAccessToken(HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if (session == null) {
            return ResponseEntity.status(401).body("세션이 존재하지 않습니다.");
        }

        String refreshToken = (String) session.getAttribute("refreshToken");

        if (refreshToken == null || !jwtUtil.validateToken(refreshToken)) {
            session.removeAttribute("refreshToken"); // 토큰 삭제
            return ResponseEntity.status(401).body("Refresh Token이 유효하지 않습니다. 다시 로그인하세요.");
        }

        String memberId = jwtUtil.getMemberId(refreshToken);
        String authId = jwtUtil.getAuthId(refreshToken);

        String newAccessToken = jwtUtil.createAccessToken(
                AccessToken.builder()
                        .memberId(memberId)
                        .build()
        );

        return ResponseEntity.ok(newAccessToken);
    }
}
