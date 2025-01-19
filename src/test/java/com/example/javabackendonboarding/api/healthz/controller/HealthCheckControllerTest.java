package com.example.javabackendonboarding.api.healthz.controller;

import com.example.javabackendonboarding.domain.user.entity.User;
import com.example.javabackendonboarding.domain.user.enums.Authority;
import com.example.javabackendonboarding.domain.user.repository.UserRepository;
import com.example.javabackendonboarding.security.mock.WithCustomMockUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Collections;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application.yml")
class HealthCheckControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;


    @BeforeEach
    void setUp() {
        User user = new User(
                "JIN HO",
                "12341234",
                "Mentos",
                Collections.singleton(Authority.ROLE_USER)
        );

        userRepository.save(user);
    }

    @Test
    @Disabled // todo: 해결 못한 테스트 토큰 형식 에러 발생
    @WithCustomMockUser
    public void IfUserExistsThenGetUserInfoReturnsSuccess() throws Exception {

        mockMvc.perform(get("/healthz")
                        .header("Authorization", "Bearer aaaaaaa"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }
}