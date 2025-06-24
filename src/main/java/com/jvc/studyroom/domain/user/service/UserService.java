package com.jvc.studyroom.domain.user.service;

import com.jvc.studyroom.domain.user.controller.dto.UserRequest;
import com.jvc.studyroom.domain.user.controller.dto.UserResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserService {
    Flux<UserResponse> getAllUsers();
    Mono<UserResponse> createUser(UserRequest request);
}