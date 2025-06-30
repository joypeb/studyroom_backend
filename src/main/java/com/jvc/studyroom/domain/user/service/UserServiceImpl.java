package com.jvc.studyroom.domain.user.service;

import com.jvc.studyroom.domain.user.dto.UserRequest;
import com.jvc.studyroom.domain.user.dto.UserResponse;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class UserServiceImpl implements UserService {
    @Override
    public Flux<UserResponse> getAllUsers(){
        return Flux.just(
            new UserResponse(1L, "test", "test@example.com")
        );
    }
    @Override
    public Mono<UserResponse> createUser(UserRequest request){
        return Mono.just(
            new UserResponse(1L, "test", "test@example.com")
        );
    }
}
