package com.jvc.studyroom.domain.user.service;

import com.jvc.studyroom.domain.user.dto.KakaoUserInfoResponseDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface LoginService {
    Mono<String> getAccessTokenFromKakao(String code);
    Flux<KakaoUserInfoResponseDto> getUserInfo(String accessToken);
}
