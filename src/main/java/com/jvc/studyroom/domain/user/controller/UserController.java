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
    public Mono<UserResponse> getMe(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return Mono.just(UserResponse.builder()
                .email(userDetails.getUser().getEmail())
                .name(userDetails.getUser().getName())
                .phoneNumber(userDetails.getUser().getPhoneNumber())
                .build()
        );
    }
}
