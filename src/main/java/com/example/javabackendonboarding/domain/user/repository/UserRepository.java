package com.example.javabackendonboarding.domain.user.repository;

import com.example.javabackendonboarding.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByUsername(String username);
}