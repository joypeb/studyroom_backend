package com.jvc.studyroom.domain.user.controller;
import com.jvc.studyroom.common.dto.PaginationRequest;
import com.jvc.studyroom.common.utils.PageableUtil;
import com.jvc.studyroom.domain.user.dto.UserResponse;
import com.jvc.studyroom.domain.user.dto.UserRoleRequest;
import com.jvc.studyroom.domain.user.service.UserService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    private final PageableUtil pageableUtil;

    @GetMapping
    public Mono<Page<UserResponse>> getAllUsers(@RequestBody PaginationRequest request) {
        return userService.findAllUsers(request);
    }

    @GetMapping("/{userId}")
    public Mono<UserResponse> getUserById(@PathVariable UUID userId) {
        return userService.findUserById(userId);
    }

    @GetMapping("/role")
    public Mono<Page<UserResponse>> getAllUsersByRole(@RequestBody UserRoleRequest request) {
        return userService.findAllUsersByRole(request);
    }
}
