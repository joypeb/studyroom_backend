package com.jvc.studyroom.domain.user.service;

import com.jvc.studyroom.common.dto.PaginationRequest;
import com.jvc.studyroom.domain.user.dto.UserResponse;
import com.jvc.studyroom.domain.user.dto.UserRoleRequest;
import com.jvc.studyroom.domain.user.dto.UserStatusRequest;
import com.jvc.studyroom.domain.user.dto.UserUpdateRequest;
import com.jvc.studyroom.domain.user.model.User;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserService {
    Mono<Page<UserResponse>> findAllUsers(PaginationRequest request, User user);
    Mono<UserResponse> findUserById(UUID userId, User user);
    Mono<Page<UserResponse>> findAllUsersByRole(UserRoleRequest request, User user);
    Mono<Integer> updateUserStatusById(UUID userId, UserStatusRequest request, User user);
    Mono<UserResponse> updateUser(UUID userId, UserUpdateRequest request, User user);
    Flux<UserResponse> findStudentsByParentId(UUID parentId, User user);
    Flux<UserResponse> findParentsByStudentId(UUID studentId, User user);
}