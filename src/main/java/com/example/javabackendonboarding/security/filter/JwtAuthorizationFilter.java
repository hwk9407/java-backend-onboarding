package com.example.javabackendonboarding.security.filter;


import com.example.javabackendonboarding.security.config.JwtUtil;
import com.example.javabackendonboarding.security.service.JwtTokenService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;

@Slf4j(topic = "JWT 검증 및 인가")
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;
    private final JwtTokenService jwtTokenService;

    public JwtAuthorizationFilter(JwtUtil jwtUtil, UserDetailsService userDetailsService, JwtTokenService jwtTokenService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
        this.jwtTokenService = jwtTokenService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain filterChain) throws ServletException, IOException {
        String path = req.getRequestURI();
        if (path.startsWith("/signup") || path.startsWith("/sign")) {
            filterChain.doFilter(req, res);
            return;
        }
        String bearerJwt = req.getHeader(jwtUtil.getAccessHeader());

        if (bearerJwt == null) {
            authFailResponse(res, "로그인이 필요합니다.");
            return;
        }
        String tokenValue = null;
        if (StringUtils.hasText(bearerJwt) && bearerJwt.startsWith("Bearer ")) {
            tokenValue = bearerJwt.substring("Bearer ".length());
        }

        if (StringUtils.hasText(tokenValue)) {
            try {
                Claims claims = jwtUtil.validateAccessToken(tokenValue);
                setAuthentication(claims.get("username", String.class));
            } catch (ExpiredJwtException e) {
                Cookie[] cookies = req.getCookies();
                if (cookies != null) {
                    String refreshToken = Arrays.stream(cookies)
                            .filter(cookie -> "refresh_token".equals(cookie.getName()))
                            .map(Cookie::getValue) // 쿠키의 값을 추출
                            .findFirst() // 첫 번째 매칭되는 값을 찾음
                            .orElse(null);

                    if (refreshToken == null) {
                        authFailResponse(res, "리프레시 토큰이 없습니다. 다시 로그인 해주세요.");
                        return;
                    }
                    try {
                        jwtUtil.validateRefreshToken(refreshToken);
                        String newAccessToken = jwtTokenService.reissueToken(refreshToken);
                        jwtUtil.addAccessTokenToHeader(res, newAccessToken);
                        Claims claims = jwtUtil.validateAccessToken(newAccessToken.substring("Bearer ".length()));
                        setAuthentication(claims.get("username", String.class));
                    } catch (Exception ex) {
                        authFailResponse(res, "리프레시 토큰이 유효하지 않거나 잘못된 형식입니다. 다시 로그인 해주세요.");
                        return;
                    }
                } else {
                    authFailResponse(res, "토큰이 만료되었습니다. 다시 로그인 해주세요.");
                    return;
                }

            } catch (Exception e) {
                authFailResponse(res, "잘못된 형태의 토큰입니다. 다시 로그인 해주세요.");
                return;
            }
        }

        filterChain.doFilter(req, res);
    }

    // 인증 처리
    public void setAuthentication(String email) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        Authentication authentication = createAuthentication(email);
        context.setAuthentication(authentication);

        SecurityContextHolder.setContext(context);
    }

    // 인증 객체 생성
    private Authentication createAuthentication(String email) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }

    private void authFailResponse(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401
        response.setContentType("application/json;charset=UTF-8");
        String jsonResponse = "{\"error\": \"" + message + "\"}";
        response.getWriter().write(jsonResponse); // 응답 본문에 JSON 메시지 작성
    }
}