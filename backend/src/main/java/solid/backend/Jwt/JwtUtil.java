package solid.backend.Jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.ZonedDateTime;
import java.util.Date;

@Slf4j
@Component
public class JwtUtil {
    private final Key key;
    private final long accessTokenExpTime;
    private final long refreshTokenExpTime;

    public JwtUtil(
            @Value("${jwt.secret}") String secretKey,
            @Value("${jwt.expiration_time}") long accessTokenExpTime,
            @Value("${jwt.refresh_expiration_time}") long refreshTokenExpTime
    ) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.accessTokenExpTime = accessTokenExpTime;
        this.refreshTokenExpTime = refreshTokenExpTime;
    }

    /**
     * 설명: Access Token 생성
     * @param accessToken
     * @return createAccess() - Access Token 생성 메서드
     */
    public String createAccessToken(AccessToken accessToken) {
        return createAccess(accessToken, accessTokenExpTime);
    }

    /**
     * 설명: Refresh Token 생성
     * @param accessToken
     * @return createRefresh() - Refresh Token 생성 메서드
     */
    public String createRefreshToken(AccessToken accessToken) {
        return createRefresh(accessToken, refreshTokenExpTime);
    }


    /**
     * 설명: JWT 생성
     * @param accessToken
     * @param expireTime
     * @return JWT String
     */
    private String createAccess(AccessToken accessToken, long expireTime) {
        Claims claims = Jwts.claims();
        claims.put("memberId", accessToken.getMemberId());

        ZonedDateTime now = ZonedDateTime.now();
        ZonedDateTime tokenValidity = now.plusSeconds(expireTime);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(Date.from(now.toInstant()))
                .setExpiration(Date.from(tokenValidity.toInstant()))
                .signWith(key)
                .compact();
    }

    /**
     * 설명: Refresh Token 생성
     * @param accessToken
     * @param refreshTokenExpTime
     * @return refreshToken
     */
    public String createRefresh(AccessToken accessToken, long refreshTokenExpTime) {
        Claims claims = Jwts.claims();
        claims.put("memberId", accessToken.getMemberId());
        claims.put("type", "refresh");

        ZonedDateTime now = ZonedDateTime.now();
        ZonedDateTime tokenValidity = now.plusSeconds(refreshTokenExpTime);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(Date.from(now.toInstant()))
                .setExpiration(Date.from(tokenValidity.toInstant()))
                .signWith(key)
                .compact();
    }

    /**
     * 설명: Token에서 MemberId 추출
     * @param accessToken
     * @return memberId
     */
    public String getMemberId(String accessToken) {
        return parseClaims(accessToken).get("memberId", String.class);
    }

    /**
     * 설명: JWT 검증
     * @param token
     * @return boolean
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.error("Invalid JWT token", e);
        } catch (ExpiredJwtException e) {
            log.error("Expired JWT token", e);
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT token", e);
        } catch (IllegalArgumentException e) {
            log.info("JWT claims string is empty.", e);
        }
        return false;
    }

    /**
     * 설명: JWT Claims 추출
     * @param accessToken
     * @return JWT Claims
     */
    public Claims parseClaims(String accessToken) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(accessToken)
                    .getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }

    /**
     * 설명: jwt 토큰에서 type을 꺼내서 access token, refresh token 구분
     * @param token
     * @return String (Claims)
     */
    public String getTokenType(String token) {
        try {
            return parseClaims(token).get("type", String.class);
        } catch (Exception e) {
            return null;
        }
    }
}