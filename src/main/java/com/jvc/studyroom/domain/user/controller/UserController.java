package com.jvc.studyroom.domain.user.controller;
import com.jvc.studyroom.domain.user.dto.UserResponse;
import com.jvc.studyroom.domain.user.security.CustomUserDetails;
import com.jvc.studyroom.domain.user.service.UserService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @GetMapping
    public Flux<UserResponse> getAllUsers() {
        return userService.findAllUsers();
    }

    @GetMapping("/{userId}")
    public Mono<UserResponse> getUserById(@PathVariable UUID userId) {
        return userService.findUserById(userId);
    }

    @GetMapping("/me")
    public Mono<String> getMe(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return Mono.just("현재 유저: " + userDetails.getUser().getEmail() + " " + userDetails.getAuthorities() + " " + userDetails.getUser().getPhoneNumber());
    }
}
