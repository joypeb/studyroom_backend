package com.jvc.studyroom.domain.user.oauth;

import com.jvc.studyroom.domain.user.dto.KakaoUserInfoResponseDto;
import com.jvc.studyroom.domain.user.jwt.JwtUtil;
import com.jvc.studyroom.domain.user.service.KakaoLoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler implements ServerAuthenticationSuccessHandler {

    private final KakaoLoginService kakaoLoginService;
    private final JwtUtil jwtUtil;

    @Override
    public Mono<Void> onAuthenticationSuccess(WebFilterExchange webFilterExchange, Authentication authentication) {

        // 1. 인증 객체에서 OAuth2User 가져오기
        OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();

        // 2. OAuth2User의 속성에서 필요한 정보만 추출하여 DTO 객체 생성
        KakaoUserInfoResponseDto kakaoUserInfoResponseDto = createKakaoUserInfoResponseDto(oauth2User.getAttributes());

        // 3. DB에 사용자 정보 저장 또는 업데이트
        return kakaoLoginService.createUserByKakaoInfo(kakaoUserInfoResponseDto)
                .flatMap(dbResult -> {
                    // DB 작업이 완료된 후 JWT 토큰 생성
                    String userEmail = kakaoUserInfoResponseDto.kakaoAccount().email();
                    String serverToken = jwtUtil.createToken(userEmail);

                    // 4. 클라이언트에 보낼 사용자 정보 객체 생성
                    Map<String, Object> user = new HashMap<>();
                    user.put("name", kakaoUserInfoResponseDto.kakaoAccount().name());
                    user.put("email", userEmail);
                    user.put("phoneNumber", kakaoUserInfoResponseDto.kakaoAccount().phoneNumber());

                    // 5. JWT 토큰과 사용자 정보를 쿼리 파라미터에 담아 클라이언트로 리다이렉트
                    URI location = UriComponentsBuilder.fromUriString("http://localhost:5177")
                            .queryParam("token", serverToken)
                            .queryParam("user", user.toString())
                            .build().toUri();

                    webFilterExchange.getExchange().getResponse().getHeaders().setLocation(location);
                    return webFilterExchange.getExchange().getResponse().setComplete();
                });
    }

    // ---

    // OAuth2User의 속성을 기반으로 KakaoUserInfoResponseDto를 생성하는 헬퍼 메서드
    private KakaoUserInfoResponseDto createKakaoUserInfoResponseDto(Map<String, Object> attributes) {

        Map<String, Object> kakaoAccountMap = (Map<String, Object>) attributes.get("kakao_account");

        String userEmail = (String) kakaoAccountMap.get("email");
        String userName = (String) kakaoAccountMap.get("name");
        String userPhoneNumber = (String) kakaoAccountMap.get("phone_number");

        // 필요한 정보만 담은 KakaoAccountDto 생성
        KakaoUserInfoResponseDto.KakaoAccount kakaoAccount = new KakaoUserInfoResponseDto.KakaoAccount(
                null, null, null, null, null, userName, null, null, null, userEmail, null, null, null, null, null, null, null, null, null, null, userPhoneNumber, null, null, null
        );

        // id와 kakaoAccount만 담은 최종 DTO 객체 반환
        return new KakaoUserInfoResponseDto(
                (Long) attributes.get("id"),
                null, null, null, null, kakaoAccount, null
        );
    }
}