package com.example.javabackendonboarding.domain.user.entity;

import com.example.javabackendonboarding.domain.user.enums.Authority;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Set;

@Getter
@Entity(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String nickname;

    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    private Set<Authority> authorityName;

    public User(String username, String password, String nickname, Set<Authority> authorityName) {
        this.username = username;
        this.password = password;
        this.nickname = nickname;
        this.authorityName = authorityName;
    }
}
