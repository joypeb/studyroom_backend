package com.jvc.studyroom.domain.user.service;

import com.jvc.studyroom.domain.user.dto.UserRequest;
import com.jvc.studyroom.domain.user.dto.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class BasicUserService implements UserService{

    @Override
    public Flux<UserResponse> getAllUsers() {
        return null;
    }
    @Override
    public Mono<UserResponse> createUser(UserRequest request) {
        return null;
    }
}
