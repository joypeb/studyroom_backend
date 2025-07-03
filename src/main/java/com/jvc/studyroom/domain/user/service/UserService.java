package com.jvc.studyroom.domain.user.service;

import com.jvc.studyroom.domain.user.dto.UserResponse;
import java.util.UUID;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserService {
    Flux<UserResponse> findAllUsers();
    Mono<UserResponse> findUserById(UUID userId);
}