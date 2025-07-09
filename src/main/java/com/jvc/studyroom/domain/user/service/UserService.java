package com.jvc.studyroom.domain.user.service;

import com.jvc.studyroom.domain.user.dto.UserResponse;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Mono;

public interface UserService {
    Mono<Page<UserResponse>> findAllUsers(Pageable pageable);
    Mono<UserResponse> findUserById(UUID userId);
    Mono<Page<UserResponse>> findAllUsersByRole(String role, Pageable pageable);
}