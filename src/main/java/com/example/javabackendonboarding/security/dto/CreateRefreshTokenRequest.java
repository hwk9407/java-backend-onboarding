package com.example.javabackendonboarding.security.dto;

import com.example.javabackendonboarding.domain.user.entity.User;

import java.time.LocalDateTime;

public record CreateRefreshTokenRequest (
        String tokenValue,
        User user,
        LocalDateTime expiryTime
) {
}
