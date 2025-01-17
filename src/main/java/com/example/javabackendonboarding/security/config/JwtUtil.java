package com.example.javabackendonboarding.security.config;

import com.example.javabackendonboarding.domain.user.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.Map;

@Component
@Getter
public class JwtUtil {

    private static final long EXPIRE_TOKEN_TIME = 60 * 60 * 1000L; // 60분
    private Key key;

    @Value("${jwt.header}")
    private String accessHeader;

    @Value("${jwt.secret-key}")
    private String secretKey;



    @PostConstruct
    public void init() {
        byte[] bytes = Base64.getDecoder().decode(secretKey);
        key = Keys.hmacShaKeyFor(bytes);
    }

    public String generateAccessToken(User user) {
        return "Bearer " + Jwts.builder()
                .subject(user.getId().toString())
                .claims(Map.of(
                        "username", user.getUsername(),
                        "nickname", user.getNickname(),
                        "roles", user.getAuthorityName()
                ))
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + EXPIRE_TOKEN_TIME))
                .signWith(key)
                .compact();
    }

    public void addAccessTokenToHeader(HttpServletResponse response, String accessToken) {
        response.addHeader(accessHeader, "Bearer " + accessToken);
    }

    public Claims validate(String accessToken) {
        return Jwts.parser()
                .verifyWith((SecretKey) key)
                .build()
                .parseSignedClaims(accessToken)
                .getPayload();
    }

    public Long extractUserId(String accessToken) {
        String subject = Jwts.parser()
                .verifyWith((SecretKey) key)
                .build()
                .parseSignedClaims(accessToken)
                .getPayload()
                .getSubject();
        return Long.valueOf(subject);
    }
}
