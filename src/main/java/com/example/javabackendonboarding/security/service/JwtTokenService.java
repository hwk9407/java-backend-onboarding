package com.example.javabackendonboarding.security.service;

import com.example.javabackendonboarding.domain.refreshToken.entity.RefreshToken;
import com.example.javabackendonboarding.domain.refreshToken.repository.RefreshTokenRepository;
import com.example.javabackendonboarding.security.config.JwtUtil;
import com.example.javabackendonboarding.security.dto.CreateRefreshTokenRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class JwtTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtUtil jwtUtil;

    @Transactional(readOnly = true)
    public String reissueToken(String refreshToken) {
        RefreshToken rToken = refreshTokenRepository.findByTokenValue(refreshToken)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 토큰입니다."));
        if (rToken.getExpiryTime().isBefore(LocalDateTime.now())) {
            refreshTokenRepository.deleteByTokenValue(refreshToken);
            throw new IllegalArgumentException("리프레시 토큰이 만료되었습니다. 재로그인이 필요합니다.");
        }

        return jwtUtil.generateAccessToken(rToken.getUser());
    }

    @Transactional
    public void createRefreshToken(CreateRefreshTokenRequest reqDto) {
        RefreshToken rToken = refreshTokenRepository.findByUser(reqDto.user())
                .map(existingToken -> {
                    existingToken.updateToken(reqDto.tokenValue(), reqDto.expiryTime());
                    return existingToken;
                })
                .orElse(RefreshToken.builder()
                        .tokenValue(reqDto.tokenValue())
                        .expiryTime(reqDto.expiryTime())
                        .user(reqDto.user())
                        .build());

        refreshTokenRepository.save(rToken);
    }
}
