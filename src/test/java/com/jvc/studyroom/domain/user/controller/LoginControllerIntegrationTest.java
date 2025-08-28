package com.jvc.studyroom.domain.user.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import com.jvc.studyroom.domain.user.dto.KakaoUserInfoResponseDto;
import com.jvc.studyroom.domain.user.dto.TokenResponse;
import com.jvc.studyroom.domain.user.jwt.JwtUtil;
import com.jvc.studyroom.domain.user.model.User;
import com.jvc.studyroom.domain.user.security.CustomUserDetails;
import com.jvc.studyroom.domain.user.service.LoginService;
import java.time.OffsetDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

/**
 * LoginController 통합 테스트
 * Controller와 Service 간의 통합 동작을 검증
 * DB 종속성 없이 Mock을 활용한 테스트
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("LoginController 통합 테스트")
class LoginControllerIntegrationTest {

  @Mock
  private LoginService loginService;

  @Mock
  private JwtUtil jwtUtil;

  private LoginController loginController;

  private KakaoUserInfoResponseDto mockKakaoUserInfo;
  private TokenResponse mockTokenResponse;
  private User mockUser;
  private CustomUserDetails mockUserDetails;

  @BeforeEach
  void setUp() {
    loginController = new LoginController(loginService, jwtUtil);
    setupMockData();
  }

  private void setupMockData() {
    // KakaoUserInfo Mock 데이터
    KakaoUserInfoResponseDto.KakaoAccount.Profile profile = 
        new KakaoUserInfoResponseDto.KakaoAccount.Profile(
            "통합테스트사용자", "thumb_url", "profile_url", "false", false
        );
    
    KakaoUserInfoResponseDto.KakaoAccount kakaoAccount = 
        new KakaoUserInfoResponseDto.KakaoAccount(
            true, true, true, profile, true, "통합테스트사용자", 
            true, true, true, "integration@example.com", 
            false, null, false, null, false, null, null, 
            false, null, false, "+82 10-5555-6666", false, null, null
        );
    
    mockKakaoUserInfo = new KakaoUserInfoResponseDto(
        987654L, true, new Date(), new Date(), 
        new HashMap<>(), kakaoAccount, 
        new KakaoUserInfoResponseDto.Partner("integration_uuid")
    );

    // TokenResponse Mock 데이터
    mockTokenResponse = new TokenResponse("integration_access_token", "integration_refresh_token");

    // User Mock 데이터
    mockUser = new User();
    mockUser.setUserId(UUID.randomUUID());
    mockUser.setEmail("integration@example.com");
    mockUser.setName("통합테스트사용자");
    mockUser.setPhoneNumber("010-5555-6666");
    mockUser.setCreatedAt(OffsetDateTime.now());

    // CustomUserDetails Mock 데이터
    mockUserDetails = new CustomUserDetails(mockUser);
  }

  @Test
  @DisplayName("통합 테스트: 완전한 카카오 로그인 플로우")
  void integrationTest_CompleteKakaoLoginFlow() {
    // given
    Map<String, String> requestBody = new HashMap<>();
    requestBody.put("code", "integration_test_code");

    // LoginService Mock 설정
    when(loginService.getAccessTokenFromKakao("integration_test_code"))
        .thenReturn(Mono.just(mockTokenResponse));
    when(loginService.getUserInfo("integration_access_token"))
        .thenReturn(Mono.just(mockKakaoUserInfo));
    when(loginService.createUserByKakaoInfo(mockKakaoUserInfo))
        .thenReturn(Mono.just("Integration Success"));
    
    // JwtUtil Mock 설정
    when(jwtUtil.createToken("integration@example.com"))
        .thenReturn("integration_jwt_token");

    // when
    Mono<ResponseEntity<Map<String, Object>>> result = 
        loginController.kakaoLogin(requestBody);

    // then - 전체 플로우 검증
    StepVerifier.create(result)
        .assertNext(response -> {
          // HTTP 상태 코드 검증
          assert response.getStatusCode().is2xxSuccessful();
          
          // 응답 본문 구조 검증
          Map<String, Object> body = response.getBody();
          assert body != null;
          assert body.containsKey("token");
          assert body.containsKey("user");
          
          // JWT 토큰 검증
          assert "integration_jwt_token".equals(body.get("token"));
          
          // 사용자 정보 검증
          Map<String, Object> user = (Map<String, Object>) body.get("user");
          assert user != null;
          assert user.containsKey("name");
          assert user.containsKey("email");
          assert "통합테스트사용자".equals(user.get("name"));
          assert "integration@example.com".equals(user.get("email"));
        })
        .verifyComplete();
  }

  @Test
  @DisplayName("통합 테스트: 서비스 계층 오류 전파 검증")
  void integrationTest_ServiceErrorPropagation() {
    // given
    Map<String, String> requestBody = new HashMap<>();
    requestBody.put("code", "error_test_code");

    // LoginService에서 오류 발생 시나리오
    when(loginService.getAccessTokenFromKakao("error_test_code"))
        .thenReturn(Mono.error(new RuntimeException("Integration Test Error")));

    // when
    Mono<ResponseEntity<Map<String, Object>>> result = 
        loginController.kakaoLogin(requestBody);

    // then - 오류 처리 검증
    StepVerifier.create(result)
        .assertNext(response -> {
          assert response.getStatusCode().is5xxServerError();
          Map<String, Object> body = response.getBody();
          assert body != null;
          assert body.containsKey("error");
          assert "Failed to process login.".equals(body.get("error"));
        })
        .verifyComplete();
  }

  @Test
  @DisplayName("통합 테스트: 프로필 조회 완전 플로우")
  void integrationTest_CompleteProfileFlow() {
    // given
    String authHeader = "Bearer integration_jwt_token";
    
    // JwtUtil Mock 설정
    when(jwtUtil.isTokenValid("integration_jwt_token")).thenReturn(true);

    // when
    Mono<ResponseEntity<Map<String, String>>> result = 
        loginController.getUserProfile(authHeader, mockUserDetails);

    // then
    StepVerifier.create(result)
        .assertNext(response -> {
          assert response.getStatusCode().is2xxSuccessful();
          Map<String, String> body = response.getBody();
          assert body != null;
          assert "통합테스트사용자".equals(body.get("name"));
          assert "integration@example.com".equals(body.get("email"));
        })
        .verifyComplete();
  }

  @Test
  @DisplayName("통합 테스트: 다중 사용자 시나리오")
  void integrationTest_MultipleUserScenario() {
    // 첫 번째 사용자
    testUserScenario("first_code", "first@example.com", "첫번째사용자", "first_jwt");
    
    // 두 번째 사용자
    testUserScenario("second_code", "second@example.com", "두번째사용자", "second_jwt");
  }

  private void testUserScenario(String code, String email, String name, String expectedJwt) {
    // given
    Map<String, String> requestBody = new HashMap<>();
    requestBody.put("code", code);

    // Mock 데이터 생성
    KakaoUserInfoResponseDto.KakaoAccount.Profile profile = 
        new KakaoUserInfoResponseDto.KakaoAccount.Profile(
            name, "thumb", "profile", "false", false
        );
    
    KakaoUserInfoResponseDto.KakaoAccount kakaoAccount = 
        new KakaoUserInfoResponseDto.KakaoAccount(
            true, true, true, profile, true, name, 
            true, true, true, email, 
            false, null, false, null, false, null, null, 
            false, null, false, null, false, null, null
        );
    
    KakaoUserInfoResponseDto userInfo = new KakaoUserInfoResponseDto(
        12345L, true, new Date(), new Date(), 
        new HashMap<>(), kakaoAccount, 
        new KakaoUserInfoResponseDto.Partner("uuid")
    );

    TokenResponse tokenResponse = new TokenResponse("access_token", "refresh_token");

    // Mock 설정
    when(loginService.getAccessTokenFromKakao(code))
        .thenReturn(Mono.just(tokenResponse));
    when(loginService.getUserInfo("access_token"))
        .thenReturn(Mono.just(userInfo));
    when(loginService.createUserByKakaoInfo(userInfo))
        .thenReturn(Mono.just("Success"));
    when(jwtUtil.createToken(email))
        .thenReturn(expectedJwt);

    // when
    Mono<ResponseEntity<Map<String, Object>>> result = 
        loginController.kakaoLogin(requestBody);

    // then
    StepVerifier.create(result)
        .assertNext(response -> {
          assert response.getStatusCode().is2xxSuccessful();
          Map<String, Object> body = response.getBody();
          assert body != null;
          assert expectedJwt.equals(body.get("token"));
          
          Map<String, Object> user = (Map<String, Object>) body.get("user");
          assert name.equals(user.get("name"));
          assert email.equals(user.get("email"));
        })
        .verifyComplete();
  }

  @Test
  @DisplayName("통합 테스트: 에러 체인 검증")
  void integrationTest_ErrorChainValidation() {
    // given
    Map<String, String> requestBody = new HashMap<>();
    requestBody.put("code", "chain_error_code");

    // 단계별 오류 시나리오들
    
    // 1. 토큰 획득 실패
    when(loginService.getAccessTokenFromKakao("chain_error_code"))
        .thenReturn(Mono.error(new RuntimeException("Token Error")));
    
    StepVerifier.create(loginController.kakaoLogin(requestBody))
        .assertNext(response -> {
          assert response.getStatusCode().is5xxServerError();
          Map<String, Object> body = response.getBody();
          assert "Failed to process login.".equals(body.get("error"));
        })
        .verifyComplete();

    // 2. 사용자 정보 획득 실패  
    when(loginService.getAccessTokenFromKakao("chain_error_code"))
        .thenReturn(Mono.just(mockTokenResponse));
    when(loginService.getUserInfo(anyString()))
        .thenReturn(Mono.error(new RuntimeException("UserInfo Error")));
    
    StepVerifier.create(loginController.kakaoLogin(requestBody))
        .assertNext(response -> {
          assert response.getStatusCode().is5xxServerError();
          Map<String, Object> body = response.getBody();
          assert "Failed to process login.".equals(body.get("error"));
        })
        .verifyComplete();

    // 3. 사용자 생성 실패
    when(loginService.getUserInfo(anyString()))
        .thenReturn(Mono.just(mockKakaoUserInfo));
    when(loginService.createUserByKakaoInfo(any(KakaoUserInfoResponseDto.class)))
        .thenReturn(Mono.error(new RuntimeException("CreateUser Error")));
    
    StepVerifier.create(loginController.kakaoLogin(requestBody))
        .assertNext(response -> {
          assert response.getStatusCode().is5xxServerError();
          Map<String, Object> body = response.getBody();
          assert "Failed to process login.".equals(body.get("error"));
        })
        .verifyComplete();
  }

  @Test
  @DisplayName("통합 테스트: 비동기 스트림 처리 검증")
  void integrationTest_ReactiveStreamProcessing() {
    // given
    Map<String, String> requestBody = new HashMap<>();
    requestBody.put("code", "reactive_test_code");

    // 비동기 체인이 정상적으로 연결되는지 검증
    when(loginService.getAccessTokenFromKakao("reactive_test_code"))
        .thenReturn(Mono.just(mockTokenResponse).delayElement(java.time.Duration.ofMillis(100)));
    when(loginService.getUserInfo("integration_access_token"))
        .thenReturn(Mono.just(mockKakaoUserInfo).delayElement(java.time.Duration.ofMillis(100)));
    when(loginService.createUserByKakaoInfo(mockKakaoUserInfo))
        .thenReturn(Mono.just("Reactive Success").delayElement(java.time.Duration.ofMillis(100)));
    when(jwtUtil.createToken("integration@example.com"))
        .thenReturn("reactive_jwt_token");

    // when & then
    StepVerifier.create(loginController.kakaoLogin(requestBody))
        .assertNext(response -> {
          assert response.getStatusCode().is2xxSuccessful();
          Map<String, Object> body = response.getBody();
          assert body != null;
          assert "reactive_jwt_token".equals(body.get("token"));
        })
        .expectComplete()
        .verify(java.time.Duration.ofSeconds(5)); // 타임아웃 설정
  }
}