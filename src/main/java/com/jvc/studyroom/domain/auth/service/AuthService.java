package com.jvc.studyroom.domain.auth.service;

import com.jvc.studyroom.domain.auth.dto.LoginRequest;
import reactor.core.publisher.Mono;

public interface AuthService {
    Mono<String> login(LoginRequest request);
}
