package com.example.javabackendonboarding.security.config;

import com.example.javabackendonboarding.domain.user.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.Key;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Date;
import java.util.Map;

@Component
@Getter
public class JwtUtil {

    private static final int EXPIRE_TOKEN_TIME = 30;
    private static final int EXPIRE_REFRESH_TOKEN_TIME = 60 * 24;
    private Key accessKey;
    private Key refreshKey;

    @Value("${jwt.header}")
    private String accessHeader;

    @Value("${jwt.secret-key}")
    private String secretKey;

    @Value("${jwt.refresh-secret-key}")
    private String refreshSecretKey;

    @PostConstruct
    public void init() {
        byte[] bytes = Base64.getDecoder().decode(secretKey);
        accessKey = Keys.hmacShaKeyFor(bytes);

        bytes = Base64.getDecoder().decode(refreshSecretKey);
        refreshKey = Keys.hmacShaKeyFor(bytes);
    }

    public String generateAccessToken(User user) {
        return "Bearer " + Jwts.builder()
                .subject(user.getId().toString())
                .claims(Map.of(
                        "username", user.getUsername(),
                        "nickname", user.getNickname(),
                        "roles", user.getAuthorityName()
                ))
                .issuedAt(Date.from(Instant.now()))
                .expiration(Date.from(Instant.now().plus(EXPIRE_TOKEN_TIME, ChronoUnit.MINUTES)))
                .signWith(accessKey)
                .compact();
    }

    public void addAccessTokenToHeader(HttpServletResponse response, String accessToken) {
        response.addHeader(accessHeader, accessToken);
    }

    public String generateRefreshToken(User user) {
        return Jwts.builder()
                .subject(user.getId().toString())
                .issuedAt(Date.from(Instant.now()))
                .expiration(Date.from(Instant.now().plus(EXPIRE_REFRESH_TOKEN_TIME, ChronoUnit.MINUTES)))
                .signWith(refreshKey)
                .compact();
    }


    public void addRefreshTokenToCookie(HttpServletResponse response, String refreshToken) {
        Cookie refreshTokenCookie = new Cookie("refresh_token", refreshToken);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setHttpOnly(true);
        // refreshTokenCookie.setSecure(true);
        refreshTokenCookie.setMaxAge(EXPIRE_TOKEN_TIME);

        response.addCookie(refreshTokenCookie);
    }

    public Claims validateAccessToken(String token) {
        return Jwts.parser()
                .verifyWith((SecretKey) accessKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public Claims validateRefreshToken(String token) {
        return Jwts.parser()
                .verifyWith((SecretKey) refreshKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public LocalDateTime getRefreshTokenExpirationTime(String refreshToken) {
        Date date = validateRefreshToken(refreshToken).getExpiration();
        return date.toInstant().atZone(ZoneId.of("Asia/Seoul")).toLocalDateTime();
    }
}
