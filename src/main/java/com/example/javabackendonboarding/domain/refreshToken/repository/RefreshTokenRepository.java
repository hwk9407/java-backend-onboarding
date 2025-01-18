package com.example.javabackendonboarding.domain.refreshToken.repository;

import com.example.javabackendonboarding.domain.refreshToken.entity.RefreshToken;
import com.example.javabackendonboarding.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByUser(User user);

    Optional<RefreshToken> findByTokenValue(String tokenValue);

    void deleteByTokenValue(String tokenValue);
}
