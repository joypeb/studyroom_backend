package com.jvc.studyroom.domain.user.service;

import com.jvc.studyroom.common.dto.PaginationRequest;
import com.jvc.studyroom.domain.user.dto.UserResponse;
import com.jvc.studyroom.domain.user.dto.UserRoleRequest;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Mono;

public interface UserService {
    Mono<Page<UserResponse>> findAllUsers(PaginationRequest request);
    Mono<UserResponse> findUserById(UUID userId);
    Mono<Page<UserResponse>> findAllUsersByRole(UserRoleRequest request);
}