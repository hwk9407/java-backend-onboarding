package com.example.javabackendonboarding.api.auth.dto.response;

import com.example.javabackendonboarding.domain.user.enums.Authority;

import java.util.List;
import java.util.Set;

public record SignupResponse(
        String username,
        String nickname,
        List<AuthorityDto> authorities
) {
}
