
package com.jvc.studyroom.domain.user.service;

import com.jvc.studyroom.domain.user.dto.KakaoTokenResponseDto;
import com.jvc.studyroom.domain.user.dto.KakaoUserInfoResponseDto;
import io.netty.handler.codec.http.HttpHeaderValues;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class KakaoLoginService implements LoginService {

    private String clientId;
    private final String KAUTH_TOKEN_URL_HOST;
    private final String KAUTH_USER_URL_HOST;

    @Autowired
    public KakaoLoginService(@Value("${kakao.client_id}") String clientId) {
        this.clientId = clientId;
        KAUTH_TOKEN_URL_HOST ="https://kauth.kakao.com";
        KAUTH_USER_URL_HOST = "https://kapi.kakao.com";
    }

    @Override
    public Mono<String> getAccessTokenFromKakao(String code) {

//        KakaoTokenResponseDto kakaoTokenResponseDto = WebClient.create(KAUTH_TOKEN_URL_HOST).post()
//                .uri(uriBuilder -> uriBuilder
//                        .scheme("https")
//                        .path("/oauth/token")
//                        .queryParam("grant_type", "authorization_code")
//                        .queryParam("client_id", clientId)
//                        .queryParam("code", code)
//                        .build(true))
//                .header(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_X_WWW_FORM_URLENCODED.toString())
//                .retrieve()
//                //TODO : Custom Exception
//                .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> Mono.error(new RuntimeException("Invalid Parameter")))
//                .onStatus(HttpStatusCode::is5xxServerError, clientResponse -> Mono.error(new RuntimeException("Internal Server Error")))
//                .bodyToMono(KakaoTokenResponseDto.class)
//                .block();
//
//
//        log.info(" [Kakao Service] Access Token ------> {}", kakaoTokenResponseDto.accessToken());
//        log.info(" [Kakao Service] Refresh Token ------> {}", kakaoTokenResponseDto.refreshToken());
//        //제공 조건: OpenID Connect가 활성화 된 앱의 토큰 발급 요청인 경우 또는 scope에 openid를 포함한 추가 항목 동의 받기 요청을 거친 토큰 발급 요청인 경우
//        log.info(" [Kakao Service] Id Token ------> {}", kakaoTokenResponseDto.idToken());
//        log.info(" [Kakao Service] Scope ------> {}", kakaoTokenResponseDto.scope());
//
//        return Mono.just(kakaoTokenResponseDto.accessToken());

        return WebClient.create(KAUTH_TOKEN_URL_HOST).post()
                .uri(uriBuilder -> uriBuilder
                        .scheme("https")
                        .path("/oauth/token")
                        .queryParam("grant_type", "authorization_code")
                        .queryParam("client_id", clientId)
                        .queryParam("code", code)
                        .build(true))
                .header(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_X_WWW_FORM_URLENCODED.toString())
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError,
                        clientResponse -> Mono.error(new RuntimeException("Invalid Parameter")))
                .onStatus(HttpStatusCode::is5xxServerError,
                        clientResponse -> Mono.error(new RuntimeException("Internal Server Error")))
                .bodyToMono(KakaoTokenResponseDto.class)
                .doOnNext(dto -> {
                    log.info(" [Kakao Service] Access Token ------> {}", dto.accessToken());
                    log.info(" [Kakao Service] Refresh Token ------> {}", dto.refreshToken());
                    log.info(" [Kakao Service] Id Token ------> {}", dto.idToken());
                    log.info(" [Kakao Service] Scope ------> {}", dto.scope());
                })
                .map(KakaoTokenResponseDto::accessToken);
    }

    @Override
    public Flux<KakaoUserInfoResponseDto> getUserInfo(String accessToken) {

//        KakaoUserInfoResponseDto userInfo = WebClient.create(KAUTH_USER_URL_HOST)
//                .get()
//                .uri(uriBuilder -> uriBuilder
//                        .scheme("https")
//                        .path("/v2/user/me")
//                        .build(true))
//                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken) // access token 인가
//                .header(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_X_WWW_FORM_URLENCODED.toString())
//                .retrieve()
//                //TODO : Custom Exception
//                .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> Mono.error(new RuntimeException("Invalid Parameter")))
//                .onStatus(HttpStatusCode::is5xxServerError, clientResponse -> Mono.error(new RuntimeException("Internal Server Error")))
//                .bodyToMono(KakaoUserInfoResponseDto.class)
//                .block();
//
//        log.info("[ Kakao Service ] Auth ID ---> {} ", userInfo.id());
//        log.info("[ Kakao Service ] NickName ---> {} ", userInfo.kakaoAccount().profile().nickName());
//        log.info("[ Kakao Service ] ProfileImageUrl ---> {} ", userInfo.kakaoAccount().profile().profileImageUrl());
//
//        return Flux.just(userInfo);
        return WebClient.create(KAUTH_USER_URL_HOST)
                .get()
                .uri(uriBuilder -> uriBuilder
                        .scheme("https")
                        .path("/v2/user/me")
                        .build(true))
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .header(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_X_WWW_FORM_URLENCODED.toString())
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError,
                        clientResponse -> Mono.error(new RuntimeException("Invalid Parameter")))
                .onStatus(HttpStatusCode::is5xxServerError,
                        clientResponse -> Mono.error(new RuntimeException("Internal Server Error")))
                .bodyToMono(KakaoUserInfoResponseDto.class)
                .doOnNext(userInfo -> {
                    log.info("[ Kakao Service ] Auth ID ---> {} ", userInfo.id());
                    log.info("[ Kakao Service ] NickName ---> {} ", userInfo.kakaoAccount().profile().nickName());
                    log.info("[ Kakao Service ] ProfileImageUrl ---> {} ", userInfo.kakaoAccount().profile().profileImageUrl());
                })
                .flux();
    }
}
