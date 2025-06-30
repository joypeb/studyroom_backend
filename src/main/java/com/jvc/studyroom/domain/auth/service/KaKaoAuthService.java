package com.jvc.studyroom.domain.auth.service;

import com.jvc.studyroom.domain.auth.dto.LoginRequest;
import com.jvc.studyroom.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class KaKaoAuthService implements AuthService{
    private final UserRepository userRepository;
    @Override
    public Mono<String> login(LoginRequest request) {
        return null;
    }
}
