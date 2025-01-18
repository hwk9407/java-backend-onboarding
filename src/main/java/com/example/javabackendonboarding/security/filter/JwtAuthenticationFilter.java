package com.example.javabackendonboarding.security.filter;

import com.example.javabackendonboarding.api.auth.dto.request.LoginRequest;
import com.example.javabackendonboarding.api.auth.dto.response.LoginResponse;
import com.example.javabackendonboarding.domain.user.entity.User;
import com.example.javabackendonboarding.security.config.JwtUtil;
import com.example.javabackendonboarding.security.dto.CreateRefreshTokenRequest;
import com.example.javabackendonboarding.security.entity.UserDetailsImpl;
import com.example.javabackendonboarding.security.service.JwtTokenService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;

@Slf4j(topic = "로그인 및 JWT 생성")
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private final JwtUtil jwtUtil;
    private final JwtTokenService jwtTokenService;

    public JwtAuthenticationFilter(JwtUtil jwtUtil, JwtTokenService jwtTokenService) {
        this.jwtUtil = jwtUtil;
        this.jwtTokenService = jwtTokenService;
        setFilterProcessesUrl("/sign");
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        try {
            LoginRequest reqDto = new ObjectMapper().readValue(request.getInputStream(), LoginRequest.class);
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    reqDto.username(),
                    reqDto.password(),
                    null
            );

            return getAuthenticationManager().authenticate(authToken);
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException {
        UserDetailsImpl userDetails = (UserDetailsImpl) authResult.getPrincipal();
        User user = userDetails.getUser();

        String token = jwtUtil.generateAccessToken(user);
        jwtUtil.addAccessTokenToHeader(response, token);

        String refreshToken = jwtUtil.generateRefreshToken(user);
        CreateRefreshTokenRequest reqDto = new CreateRefreshTokenRequest(
                refreshToken,
                user,
                jwtUtil.getRefreshTokenExpirationTime(refreshToken)
        );
        jwtUtil.addRefreshTokenToCookie(response, refreshToken);
        jwtTokenService.createRefreshToken(reqDto);

        LoginResponse loginResDto = new LoginResponse(token.substring("Bearer ".length()));
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonResponse = objectMapper.writeValueAsString(loginResDto);

        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(jsonResponse);
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"error\":\"로그인 인증 실패\"}");
    }

}