package com.example.javabackendonboarding;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
public class JavaBackendOnboardingApplication {

    public static void main(String[] args) {
        SpringApplication.run(JavaBackendOnboardingApplication.class, args);
    }

}
