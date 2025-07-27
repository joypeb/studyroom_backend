package com.jvc.studyroom.domain.user.controller;
import com.jvc.studyroom.common.dto.PaginationRequest;
import com.jvc.studyroom.common.utils.PageableUtil;
import com.jvc.studyroom.domain.user.dto.UserResponse;
import com.jvc.studyroom.domain.user.dto.UserRoleRequest;
import com.jvc.studyroom.domain.user.dto.UserStatusRequest;
import com.jvc.studyroom.domain.user.dto.UserUpdateRequest;
import com.jvc.studyroom.domain.user.service.UserService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    // 전체 유저 리스트
    @GetMapping
    public Mono<Page<UserResponse>> getAllUsers(@RequestBody PaginationRequest request) {
        return userService.findAllUsers(request);
    }

    // 특정 유저
    @GetMapping("/{userId}")
    public Mono<UserResponse> getUserById(@PathVariable UUID userId) {
        return userService.findUserById(userId);
    }

    // 특정 역할에 대한 유저 리스트
    @GetMapping("/role")
    public Mono<Page<UserResponse>> getAllUsersByRole(@RequestBody UserRoleRequest request) {
        return userService.findAllUsersByRole(request);
    }

    // 특정 유저에 대한 상태 수정
    @PutMapping("/{userId}/status")
    public Mono<Integer> updateUserStatusById(@PathVariable UUID userId, @RequestBody UserStatusRequest request) {
        return userService.updateUserStatusById(userId, request);
    }

    //유저 수정
    @PutMapping("/{userId}")
    public Mono<UserResponse> updateUser(@PathVariable UUID userId, @RequestBody UserUpdateRequest request) {
        return userService.updateUser(userId, request);
    }

    // 특정 부모에 대한 학생 리스트
    @GetMapping("/{parentId}/students")
    public Flux<UserResponse> getStudentsByParentId(@PathVariable UUID parentId) {
        return userService.findStudentsByParentId(parentId);
    }

    // 특정 학생에 대한 부모 리스트
    @GetMapping("/{studentId}/parents")
    public Flux<UserResponse> getParentsByStudentId(@PathVariable UUID studentId) {
        return userService.findParentsByStudentId(studentId);
    }
}
