package com.example.javabackendonboarding.security.config;

import com.example.javabackendonboarding.domain.user.entity.User;
import com.example.javabackendonboarding.domain.user.enums.Authority;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.Date;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class JwtUtilTest {

    @InjectMocks
    private JwtUtil jwtUtil;

    @Mock
    private HttpServletResponse response;

    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new User(
                "JIN HO",
                "12341234",
                "Mentos",
                Collections.singleton(Authority.ROLE_USER)
        );
        ReflectionTestUtils.setField(user, "id", 1L);
        ReflectionTestUtils.setField(jwtUtil, "accessHeader", "Authorization");
        ReflectionTestUtils.setField(jwtUtil, "secretKey", "yourBase64EncodedSecretKeyYourBase64EncodedSecretKey");
        ReflectionTestUtils.setField(jwtUtil, "refreshSecretKey", "yourBase64EncodedRefreshSecretKeyYourBase64EncodedRefreshSecretKey");
        jwtUtil.init();
    }

    @DisplayName("유효한 액세스 토큰이 생성되는지 테스트")
    @Test
    void testGenerateAccessToken() {
        // given

        // when
        String accessToken = jwtUtil.generateAccessToken(user);

        // then
        assertNotNull(accessToken);
        assertFalse(accessToken.isEmpty());
        assertTrue(accessToken.startsWith("Bearer "));
    }

    @DisplayName("액세스 토큰은 HTTP 응답 헤더에 추가되는지 테스트")
    @Test
    void shouldAddAccessTokenToHeader() {
        // given
        String accessToken = "Bearer testAccessToken";
        HttpServletResponse response = mock(HttpServletResponse.class);

        // when
        jwtUtil.addAccessTokenToHeader(response, accessToken);

        // then
        verify(response).addHeader("Authorization", accessToken);
    }

    @DisplayName("유효한 리프레시 토큰이 생성는지 테스트")
    @Test
    void generateRefreshTokenTest () {
        // given

        // when
        String refreshToken = jwtUtil.generateRefreshToken(user);

        // then
        assertNotNull(refreshToken);
        assertFalse(refreshToken.isEmpty());
    }

    @DisplayName("리프레시 토큰이 HTTP 응답 쿠키에 추가되는지 테스트")
    @Test
    void shouldAddRefreshTokenToCookie() {
        // given
        String refreshToken = "testRefreshToken";
        HttpServletResponse response = mock(HttpServletResponse.class);

        // when
        jwtUtil.addRefreshTokenToCookie(response, refreshToken);

        // then
        ArgumentCaptor<Cookie> cookieCaptor = ArgumentCaptor.forClass(Cookie.class);
        verify(response).addCookie(cookieCaptor.capture());
        Cookie capturedCookie = cookieCaptor.getValue();

        assertEquals("refresh_token", capturedCookie.getName());
        assertTrue(capturedCookie.isHttpOnly());
        assertEquals("/", capturedCookie.getPath());
    }

    @DisplayName("1분 지난 액세스 토큰을 검증할 때 예외가 발생하는지 테스트")
    @Test
    void testExpiredAccessToken() {
        // given
        String expiredAccessToken = Jwts.builder()
                .subject(user.getId().toString())
                .claims(Map.of(
                        "username", user.getUsername(),
                        "nickname", user.getNickname(),
                        "roles", user.getAuthorityName()
                ))
                .issuedAt(Date.from(Instant.now()))
                .expiration(Date.from(Instant.now().minus(1, ChronoUnit.MINUTES)))
                .signWith(jwtUtil.getAccessKey())
                .compact();

        // when & then
        assertThrows(ExpiredJwtException.class, () -> {
            jwtUtil.validateAccessToken(expiredAccessToken);
        });
    }

    @DisplayName("1분 지난 리프레시 토큰을 검증할 때 예외가 발생하는지 테스트")
    @Test
    void testExpiredRefreshToken() {
        // given
        String expiredRefreshToken = Jwts.builder()
                .subject(user.getId().toString())
                .issuedAt(Date.from(Instant.now()))
                .expiration(Date.from(Instant.now().minus(1, ChronoUnit.MINUTES)))
                .signWith(jwtUtil.getRefreshKey())
                .compact();

        // when & then
        assertThrows(ExpiredJwtException.class, () -> {
            jwtUtil.validateRefreshToken(expiredRefreshToken);  // 만료된 리프레시 토큰 검증
        });
    }
}