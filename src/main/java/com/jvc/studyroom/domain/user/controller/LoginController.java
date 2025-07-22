package com.jvc.studyroom.domain.user.controller;
import com.jvc.studyroom.domain.user.dto.TokenResponse;
import com.jvc.studyroom.domain.user.jwt.JwtUtil;
import com.jvc.studyroom.domain.user.service.LoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/auth/kakao")
@RequiredArgsConstructor
public class LoginController {

    private final LoginService kakaoLoginService;

    private final JwtUtil jwtUtil;

    @GetMapping("/callback")
    public Mono<String> kakaoLoginCallback(@RequestParam("code") String code) {
        return kakaoLoginService.getAccessTokenFromKakao(code)
                .flatMap(tokenResponse ->
                        kakaoLoginService.getUserInfo(tokenResponse.accessToken())
                                .flatMap(userinfo ->
                                        kakaoLoginService.createUserByKakaoInfo(userinfo)
                                                .then(Mono.just(jwtUtil.createToken(userinfo.kakaoAccount().email())))
                                )
                );
    }
}
