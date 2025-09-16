package com.jvc.studyroom.domain.user.controller;
import com.jvc.studyroom.domain.user.jwt.JwtUtil;
import com.jvc.studyroom.domain.user.security.CustomUserDetails;
import com.jvc.studyroom.domain.user.service.LoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth/kakao")
@RequiredArgsConstructor
public class LoginController {

    private final LoginService kakaoLoginService;

    private final JwtUtil jwtUtil;

    @PostMapping("/login")
    public Mono<ResponseEntity<Map<String, Object>>> kakaoLogin(@RequestBody Map<String, String> requestBody) {
        String code = requestBody.get("code");

        if (code == null) {
            return Mono.just(ResponseEntity.badRequest().body(Map.of("error", "Authorization code is missing.")));
        }

        return kakaoLoginService.getAccessTokenFromKakao(code)
                .flatMap(tokenResponse -> kakaoLoginService.getUserInfo(tokenResponse.accessToken()))
                .flatMap(userInfo ->
                        kakaoLoginService.createUserByKakaoInfo(userInfo) // 사용자 정보로 DB 저장 및 업데이트
                                .then(Mono.just(userInfo)) // 사용자 정보 스트림 유지
                )
                .map(userInfo -> {
                    // 1. 서버에서 새로운 JWT 토큰 생성
                    String serverToken = jwtUtil.createToken(userInfo.kakaoAccount().email());

                    // 2. 클라이언트에 보낼 사용자 정보 객체 생성
                    Map<String, Object> user = new HashMap<>();
                    user.put("name", userInfo.kakaoAccount().profile().nickName());
                    user.put("email", userInfo.kakaoAccount().email());

                    // 3. 토큰과 사용자 정보를 포함하는 최종 응답 맵 생성
                    Map<String, Object> response = new HashMap<>();
                    response.put("token", serverToken);
                    response.put("user", user);

                    // 4. JSON 형태로 클라이언트에 응답
                    return ResponseEntity.ok(response);
                })
                .onErrorResume(e -> {
                    // 오류 발생 시 에러 응답 반환
                    System.err.println("Login error: " + e.getMessage());
                    return Mono.just(ResponseEntity.internalServerError().body(Map.of("error", "Failed to process login.")));
                });
    }

    // 로그인된 사용자의 프로필 정보를 제공하는 API 추가
    @GetMapping("/profile")
    public Mono<ResponseEntity<Map<String, String>>> getUserProfile(@RequestHeader("Authorization") String authorizationHeader, @AuthenticationPrincipal CustomUserDetails userDetails) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return Mono.just(ResponseEntity.status(401).build());
        }

        String token = authorizationHeader.substring("Bearer ".length());

        if (jwtUtil.isTokenValid(token)) {
            Map<String, String> profile = new HashMap<>();
            profile.put("name", userDetails.getUser().getName());
            profile.put("email", userDetails.getUser().getEmail());
            return Mono.just(ResponseEntity.ok(profile));
        } else {
            return Mono.just(ResponseEntity.status(401).build());
        }
    }
}
