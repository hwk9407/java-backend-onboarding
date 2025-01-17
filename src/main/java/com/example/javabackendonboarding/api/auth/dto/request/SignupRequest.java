package com.example.javabackendonboarding.api.auth.dto.request;

import jakarta.validation.constraints.NotBlank;

public record SignupRequest(
        @NotBlank(message = "유저이름은 공백일 수 없습니다.")
        String username,

        @NotBlank(message = "비밀번호는 공백일 수 없습니다.")
        String password,

        @NotBlank(message = "닉네임은 공백일 수 없습니다.")
        String nickname
) {
}