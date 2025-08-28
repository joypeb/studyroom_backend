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
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

/**
 * LoginController 단위 테스트
 * DB 종속성 없이 Mock을 활용한 테스트
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("LoginController 단위 테스트")
class LoginControllerTest {

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
            "테스트사용자", "thumb_url", "profile_url", "false", false
        );
    
    KakaoUserInfoResponseDto.KakaoAccount kakaoAccount = 
        new KakaoUserInfoResponseDto.KakaoAccount(
            true, true, true, profile, true, "테스트사용자", 
            true, true, true, "test@example.com", 
            false, null, false, null, false, null, null, 
            false, null, false, null, false, null, null
        );
    
    mockKakaoUserInfo = new KakaoUserInfoResponseDto(
        123456L, true, new Date(), new Date(), 
        new HashMap<>(), kakaoAccount, 
        new KakaoUserInfoResponseDto.Partner("uuid")
    );

    // TokenResponse Mock 데이터
    mockTokenResponse = new TokenResponse("mock_access_token", "mock_refresh_token");

    // User Mock 데이터
    mockUser = new User();
    mockUser.setUserId(UUID.randomUUID());
    mockUser.setEmail("test@example.com");
    mockUser.setName("테스트사용자");
    mockUser.setCreatedAt(OffsetDateTime.now());

    // CustomUserDetails Mock 데이터
    mockUserDetails = new CustomUserDetails(mockUser);
  }

  @Nested
  @DisplayName("카카오 로그인 테스트")
  class KakaoLoginTest {

    @Test
    @DisplayName("성공: 정상적인 authorization code로 로그인")
    void kakaoLogin_Success() {
      // given
      Map<String, String> requestBody = new HashMap<>();
      requestBody.put("code", "valid_auth_code");

      when(loginService.getAccessTokenFromKakao("valid_auth_code"))
          .thenReturn(Mono.just(mockTokenResponse));
      when(loginService.getUserInfo("mock_access_token"))
          .thenReturn(Mono.just(mockKakaoUserInfo));
      when(loginService.createUserByKakaoInfo(mockKakaoUserInfo))
          .thenReturn(Mono.just("created"));
      when(jwtUtil.createToken("test@example.com"))
          .thenReturn("jwt_token");

      // when
      Mono<ResponseEntity<Map<String, Object>>> result = 
          loginController.kakaoLogin(requestBody);

      // then
      StepVerifier.create(result)
          .assertNext(response -> {
            assert response.getStatusCode().is2xxSuccessful();
            Map<String, Object> body = response.getBody();
            assert body != null;
            assert body.containsKey("token");
            assert body.containsKey("user");
            assert "jwt_token".equals(body.get("token"));
            
            Map<String, Object> user = (Map<String, Object>) body.get("user");
            assert "테스트사용자".equals(user.get("name"));
            assert "test@example.com".equals(user.get("email"));
          })
          .verifyComplete();
    }

    @Test
    @DisplayName("실패: authorization code가 누락된 경우")
    void kakaoLogin_Fail_MissingCode() {
      // given
      Map<String, String> requestBody = new HashMap<>();
      // code가 없음

      // when
      Mono<ResponseEntity<Map<String, Object>>> result = 
          loginController.kakaoLogin(requestBody);

      // then
      StepVerifier.create(result)
          .assertNext(response -> {
            assert response.getStatusCode().is4xxClientError();
            Map<String, Object> body = response.getBody();
            assert body != null;
            assert body.containsKey("error");
            assert "Authorization code is missing.".equals(body.get("error"));
          })
          .verifyComplete();
    }

    @Test
    @DisplayName("실패: 카카오 API 호출 중 오류 발생")
    void kakaoLogin_Fail_KakaoApiError() {
      // given
      Map<String, String> requestBody = new HashMap<>();
      requestBody.put("code", "invalid_auth_code");

      when(loginService.getAccessTokenFromKakao("invalid_auth_code"))
          .thenReturn(Mono.error(new RuntimeException("Kakao API Error")));

      // when
      Mono<ResponseEntity<Map<String, Object>>> result = 
          loginController.kakaoLogin(requestBody);

      // then
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
    @DisplayName("실패: 사용자 정보 조회 중 오류 발생")
    void kakaoLogin_Fail_UserInfoError() {
      // given
      Map<String, String> requestBody = new HashMap<>();
      requestBody.put("code", "valid_auth_code");

      when(loginService.getAccessTokenFromKakao("valid_auth_code"))
          .thenReturn(Mono.just(mockTokenResponse));
      when(loginService.getUserInfo("mock_access_token"))
          .thenReturn(Mono.error(new RuntimeException("User Info Error")));

      // when
      Mono<ResponseEntity<Map<String, Object>>> result = 
          loginController.kakaoLogin(requestBody);

      // then
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
    @DisplayName("실패: 사용자 생성 중 오류 발생")
    void kakaoLogin_Fail_CreateUserError() {
      // given
      Map<String, String> requestBody = new HashMap<>();
      requestBody.put("code", "valid_auth_code");

      when(loginService.getAccessTokenFromKakao("valid_auth_code"))
          .thenReturn(Mono.just(mockTokenResponse));
      when(loginService.getUserInfo("mock_access_token"))
          .thenReturn(Mono.just(mockKakaoUserInfo));
      when(loginService.createUserByKakaoInfo(mockKakaoUserInfo))
          .thenReturn(Mono.error(new RuntimeException("Create User Error")));

      // when
      Mono<ResponseEntity<Map<String, Object>>> result = 
          loginController.kakaoLogin(requestBody);

      // then
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
  }

  @Nested
  @DisplayName("프로필 조회 테스트")
  class GetUserProfileTest {

    @Test
    @DisplayName("성공: 유효한 JWT 토큰으로 프로필 조회")
    void getUserProfile_Success() {
      // given
      String authHeader = "Bearer valid_jwt_token";
      when(jwtUtil.isTokenValid("valid_jwt_token")).thenReturn(true);

      // when
      Mono<ResponseEntity<Map<String, String>>> result = 
          loginController.getUserProfile(authHeader, mockUserDetails);

      // then
      StepVerifier.create(result)
          .assertNext(response -> {
            assert response.getStatusCode().is2xxSuccessful();
            Map<String, String> body = response.getBody();
            assert body != null;
            assert "테스트사용자".equals(body.get("name"));
            assert "test@example.com".equals(body.get("email"));
          })
          .verifyComplete();
    }

    @Test
    @DisplayName("실패: Authorization 헤더가 누락된 경우")
    void getUserProfile_Fail_MissingAuthHeader() {
      // when
      Mono<ResponseEntity<Map<String, String>>> result = 
          loginController.getUserProfile(null, mockUserDetails);

      // then
      StepVerifier.create(result)
          .assertNext(response -> {
            assert response.getStatusCode().value() == 401;
            assert response.getBody() == null;
          })
          .verifyComplete();
    }

    @Test
    @DisplayName("실패: Bearer 토큰 형식이 아닌 경우")
    void getUserProfile_Fail_InvalidAuthHeaderFormat() {
      // given
      String authHeader = "Invalid format token";

      // when
      Mono<ResponseEntity<Map<String, String>>> result = 
          loginController.getUserProfile(authHeader, mockUserDetails);

      // then
      StepVerifier.create(result)
          .assertNext(response -> {
            assert response.getStatusCode().value() == 401;
            assert response.getBody() == null;
          })
          .verifyComplete();
    }

    @Test
    @DisplayName("실패: 유효하지 않은 JWT 토큰")
    void getUserProfile_Fail_InvalidToken() {
      // given
      String authHeader = "Bearer invalid_jwt_token";
      when(jwtUtil.isTokenValid("invalid_jwt_token")).thenReturn(false);

      // when
      Mono<ResponseEntity<Map<String, String>>> result = 
          loginController.getUserProfile(authHeader, mockUserDetails);

      // then
      StepVerifier.create(result)
          .assertNext(response -> {
            assert response.getStatusCode().value() == 401;
            assert response.getBody() == null;
          })
          .verifyComplete();
    }
  }

  @Nested
  @DisplayName("Controller 직접 호출 테스트")
  class DirectControllerTest {

    @Test
    @DisplayName("Controller 의존성 주입 검증")
    void testControllerDependencies() {
      // given & when & then
      assert loginController != null;
    }

    @Test
    @DisplayName("성공적인 로그인 플로우 전체 검증")
    void testCompleteLoginFlow() {
      // given
      Map<String, String> requestBody = new HashMap<>();
      requestBody.put("code", "complete_flow_code");

      when(loginService.getAccessTokenFromKakao(anyString()))
          .thenReturn(Mono.just(mockTokenResponse));
      when(loginService.getUserInfo(anyString()))
          .thenReturn(Mono.just(mockKakaoUserInfo));
      when(loginService.createUserByKakaoInfo(any(KakaoUserInfoResponseDto.class)))
          .thenReturn(Mono.just("user_created"));
      when(jwtUtil.createToken(anyString()))
          .thenReturn("complete_jwt_token");

      // when
      Mono<ResponseEntity<Map<String, Object>>> result = 
          loginController.kakaoLogin(requestBody);

      // then
      StepVerifier.create(result)
          .assertNext(response -> {
            assert response.getStatusCode().is2xxSuccessful();
            Map<String, Object> body = response.getBody();
            assert body != null;
            
            // 토큰 검증
            assert body.containsKey("token");
            assert "complete_jwt_token".equals(body.get("token"));
            
            // 사용자 정보 검증
            assert body.containsKey("user");
            Map<String, Object> user = (Map<String, Object>) body.get("user");
            assert user.containsKey("name");
            assert user.containsKey("email");
            assert "테스트사용자".equals(user.get("name"));
            assert "test@example.com".equals(user.get("email"));
          })
          .verifyComplete();
    }
  }
}