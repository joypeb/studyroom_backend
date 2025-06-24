package com.jvc.studyroom.domain.user.controller;
import com.jvc.studyroom.domain.user.controller.dto.UserRequest;
import com.jvc.studyroom.domain.user.controller.dto.UserResponse;
import com.jvc.studyroom.domain.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/users")
public class UserController {
    // @Autowired  // final 필드는 리플렉션으로 주입 불가 아래 생성자 방법으로
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }
    @GetMapping
    public Flux<UserResponse> getAllUsers() {
        return userService.getAllUsers();
    }
    @PostMapping
    public Mono<UserResponse> createUser(@RequestBody UserRequest request) {
        return userService.createUser(request);
    }
}
