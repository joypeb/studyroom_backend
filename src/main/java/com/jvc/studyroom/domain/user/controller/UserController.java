package com.jvc.studyroom.domain.user.controller;
import com.jvc.studyroom.common.utils.PageableUtil;
import com.jvc.studyroom.domain.user.dto.UserResponse;
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
    public Mono<Page<UserResponse>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "userId") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection) {
        Pageable pageable = pageableUtil.createPageable(page, size, sortBy, sortDirection);
        return userService.findAllUsers(pageable);
    }

    @GetMapping("/{userId}")
    public Mono<UserResponse> getUserById(@PathVariable UUID userId) {
        return userService.findUserById(userId);
    }

    @GetMapping("/role/{role}")
    public Mono<Page<UserResponse>> getAllUsersByRole(@PathVariable String role,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "userId") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection) {
        Pageable pageable = pageableUtil.createPageable(page, size, sortBy, sortDirection);
        return userService.findAllUsersByRole(role, pageable);
    }
}
