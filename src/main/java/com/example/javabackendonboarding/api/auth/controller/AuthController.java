package com.example.javabackendonboarding.api.auth.controller;

import com.example.javabackendonboarding.api.auth.dto.request.SignupRequest;
import com.example.javabackendonboarding.api.auth.dto.response.SignupResponse;
import com.example.javabackendonboarding.api.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;


    @PostMapping("/signup")
    public ResponseEntity<SignupResponse> signup(@RequestBody @Valid SignupRequest reqDto) {
        SignupResponse resDto = authService.signup(reqDto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(resDto);
    }
}
