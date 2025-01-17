package com.example.javabackendonboarding.security.service;

import com.example.javabackendonboarding.domain.user.entity.User;
import com.example.javabackendonboarding.domain.user.repository.UserRepository;
import com.example.javabackendonboarding.security.entity.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("찾을 수 없는 아이디: " + username));

        return new UserDetailsImpl(user);
    }
}
