package solid.backend.common.jwt;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Spring Security 관련 유틸리티 클래스
 */
public class SecurityUtils {
    
    private SecurityUtils() {
        // 유틸리티 클래스는 인스턴스화 방지
    }
    
    /**
     * 현재 인증된 사용자의 ID 조회
     * 
     * @return 사용자 ID (인증되지 않은 경우 null)
     */
    public static String getCurrentMemberId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication != null && authentication.isAuthenticated()) {
            return authentication.getName();
        }
        
        return null;
    }
    
    /**
     * 현재 사용자가 인증되었는지 확인
     * 
     * @return 인증된 경우 true
     */
    public static boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.isAuthenticated();
    }
}