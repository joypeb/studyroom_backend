package com.jvc.studyroom.domain.user.controller;
import com.jvc.studyroom.domain.user.dto.KakaoUserInfoResponseDto;
import com.jvc.studyroom.domain.user.service.LoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

//@RestController
@RequestMapping("/auth/kakao")
@RequiredArgsConstructor
public class LoginController {

    //private final LoginService kakaoLoginService;

/*    @Value("${kakao.client_id}")
    private String client_id;

    @Value("${kakao.redirect_uri}")
    private String redirect_uri;*/
/*
    @GetMapping("/callback")
    public Mono<ResponseEntity<Flux<KakaoUserInfoResponseDto>>> kakaoLoginCallback(@RequestParam("code") String code) {
        return kakaoLoginService.getAccessTokenFromKakao(code)
                .flatMap(token -> {
                    Flux<KakaoUserInfoResponseDto> userInfo = kakaoLoginService.getUserInfo(token);
                    return Mono.just(ResponseEntity.ok(userInfo));
                });
    }
    */

}
