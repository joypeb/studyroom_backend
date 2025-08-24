package com.jvc.studyroom.domain.user.service;

import com.jvc.studyroom.domain.user.dto.KakaoUserInfoResponseDto;
import com.jvc.studyroom.domain.user.dto.TokenResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface LoginService {
    Mono<TokenResponse> getAccessTokenFromKakao(String code);
    Mono<KakaoUserInfoResponseDto> getUserInfo(String accessToken);
    Mono<String> createUserByKakaoInfo(KakaoUserInfoResponseDto kakaoUserInfoResponseDto);
}
