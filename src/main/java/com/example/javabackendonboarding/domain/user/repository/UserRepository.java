package com.example.javabackendonboarding.domain.user.repository;

import com.example.javabackendonboarding.domain.user.entity.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByUsername(String username);

    @EntityGraph(attributePaths = "authorityName")
    Optional<User> findByUsername(String username);
}