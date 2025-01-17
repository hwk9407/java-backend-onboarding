package com.example.javabackendonboarding.api.auth.service;

import com.example.javabackendonboarding.api.auth.dto.request.SignupRequest;
import com.example.javabackendonboarding.api.auth.dto.response.AuthorityDto;
import com.example.javabackendonboarding.api.auth.dto.response.SignupResponse;
import com.example.javabackendonboarding.domain.user.entity.User;
import com.example.javabackendonboarding.domain.user.enums.Authority;
import com.example.javabackendonboarding.domain.user.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public SignupResponse signup(@Valid SignupRequest reqDto) {
        if (userRepository.existsByUsername(reqDto.username())) {
            throw new IllegalArgumentException("이미 존재하는 유저이름입니다.");
        }
        String EncodePassword = passwordEncoder.encode(reqDto.password());
        User newUser = new User(
                reqDto.username(),
                EncodePassword,
                reqDto.nickname(),
                Collections.singleton(Authority.ROLE_USER)  // todo: User 역할로만 회원가입 가능하게 하드코딩 되어있음
        );

        User registeredUser = userRepository.save(newUser);

        return new SignupResponse(
                registeredUser.getUsername(),
                registeredUser.getNickname(),
                registeredUser.getAuthorityName().stream()
                        .map(authority -> new AuthorityDto(authority.name()))
                        .toList()
        );
    }
}
