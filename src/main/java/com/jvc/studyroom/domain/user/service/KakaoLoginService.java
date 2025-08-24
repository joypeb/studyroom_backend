
package com.jvc.studyroom.domain.user.service;

import com.jvc.studyroom.domain.user.dto.KakaoTokenResponseDto;
import com.jvc.studyroom.domain.user.dto.KakaoUserInfoResponseDto;
import com.jvc.studyroom.domain.user.dto.TokenResponse;
import com.jvc.studyroom.domain.user.model.User;
import com.jvc.studyroom.domain.user.repository.UserRepository;
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

    private final String clientId;
    private final String KAUTH_TOKEN_URL_HOST;
    private final String KAUTH_USER_URL_HOST;

    private final UserRepository userRepository;

    @Autowired
    public KakaoLoginService(@Value("${kakao.client_id}") String clientId, UserRepository userRepository) {
        this.clientId = clientId;
        this.userRepository = userRepository;
        KAUTH_TOKEN_URL_HOST ="https://kauth.kakao.com";
        KAUTH_USER_URL_HOST = "https://kapi.kakao.com";
    }

    @Override
    public Mono<TokenResponse> getAccessTokenFromKakao(String code) {
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
                .map(KakaoTokenResponseDto -> new TokenResponse(KakaoTokenResponseDto.accessToken(), KakaoTokenResponseDto.refreshToken()));
    }

    @Override
    public Mono<KakaoUserInfoResponseDto> getUserInfo(String accessToken) {
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
                });
    }

    @Override
    public Mono<String> createUserByKakaoInfo(KakaoUserInfoResponseDto kakaoUserInfoResponseDto) {
        // Kakao에서 리턴받은 이메일이 DB에 있는지 체크
        // 없으면 저장, 있으면 토큰만 리턴
        return userRepository.countByEmailAndDeletedAtIsNull(kakaoUserInfoResponseDto.kakaoAccount().email())
                .flatMap(count -> {
                    if (count == 0) {
                        User user = User.builder()
                                .email(kakaoUserInfoResponseDto.kakaoAccount().email())
                                .name(kakaoUserInfoResponseDto.kakaoAccount().name())
                                .phoneNumber(changePhoneNumberFormat(kakaoUserInfoResponseDto.kakaoAccount().phoneNumber()))
                                .build();

                        return userRepository.save(user)
                                .then(Mono.just("Success"));
                    } else {
                        return Mono.just("Success");
                    }
                });
    }

    // 전화번호 형식 변환 (+82 10-1234-5678 -> 010-1234-5678)
    public String changePhoneNumberFormat(String phoneNumber) {
        if (phoneNumber.startsWith("+82")) {
            return phoneNumber.replace("+82 ", "0");
        }
        return phoneNumber; // 이미 로컬 형식이면 그대로 반환
    }
}
