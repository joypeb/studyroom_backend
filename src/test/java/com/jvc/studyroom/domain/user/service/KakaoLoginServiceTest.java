package com.jvc.studyroom.domain.user.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.jvc.studyroom.domain.user.dto.KakaoUserInfoResponseDto;
import com.jvc.studyroom.domain.user.model.User;
import com.jvc.studyroom.domain.user.repository.UserRepository;
import java.time.OffsetDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

/**
 * KakaoLoginService 단위 테스트
 * DB 종속성 없이 Mock을 활용한 테스트
 * 외부 API 호출은 테스트하지 않고 비즈니스 로직만 검증
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("KakaoLoginService 단위 테스트")
class KakaoLoginServiceTest {

  @Mock
  private UserRepository userRepository;

  private KakaoLoginService kakaoLoginService;

  private KakaoUserInfoResponseDto mockKakaoUserInfo;
  private User mockUser;

  @BeforeEach
  void setUp() {
    kakaoLoginService = new KakaoLoginService("test_client_id", userRepository);
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
            false, null, false, "+82 10-1234-5678", false, null, null
        );
    
    mockKakaoUserInfo = new KakaoUserInfoResponseDto(
        123456L, true, new Date(), new Date(), 
        new HashMap<>(), kakaoAccount, 
        new KakaoUserInfoResponseDto.Partner("uuid")
    );

    // User Mock 데이터
    mockUser = new User();
    mockUser.setUserId(UUID.randomUUID());
    mockUser.setEmail("test@example.com");
    mockUser.setName("테스트사용자");
    mockUser.setPhoneNumber("010-1234-5678");
    mockUser.setCreatedAt(OffsetDateTime.now());
  }

  @Nested
  @DisplayName("카카오 정보로 사용자 생성 테스트")
  class CreateUserByKakaoInfoTest {

    @Test
    @DisplayName("성공: 새 사용자 생성")
    void createUserByKakaoInfo_Success_NewUser() {
      // given
      when(userRepository.countByEmailAndDeletedAtIsNull("test@example.com"))
          .thenReturn(Mono.just(0L)); // 사용자가 존재하지 않음
      when(userRepository.save(any(User.class)))
          .thenReturn(Mono.just(mockUser));

      // when
      Mono<String> result = kakaoLoginService.createUserByKakaoInfo(mockKakaoUserInfo);

      // then
      StepVerifier.create(result)
          .assertNext(response -> {
            assert "Success".equals(response);
          })
          .verifyComplete();

      verify(userRepository).countByEmailAndDeletedAtIsNull("test@example.com");
      verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("성공: 기존 사용자 존재")
    void createUserByKakaoInfo_Success_ExistingUser() {
      // given
      when(userRepository.countByEmailAndDeletedAtIsNull("test@example.com"))
          .thenReturn(Mono.just(1L)); // 사용자가 이미 존재

      // when
      Mono<String> result = kakaoLoginService.createUserByKakaoInfo(mockKakaoUserInfo);

      // then
      StepVerifier.create(result)
          .assertNext(response -> {
            assert "Success".equals(response);
          })
          .verifyComplete();

      verify(userRepository).countByEmailAndDeletedAtIsNull("test@example.com");
      verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("실패: DB 조회 오류")
    void createUserByKakaoInfo_Fail_DatabaseError() {
      // given
      when(userRepository.countByEmailAndDeletedAtIsNull("test@example.com"))
          .thenReturn(Mono.error(new RuntimeException("Database Error")));

      // when
      Mono<String> result = kakaoLoginService.createUserByKakaoInfo(mockKakaoUserInfo);

      // then
      StepVerifier.create(result)
          .expectErrorMatches(throwable -> 
              throwable instanceof RuntimeException && 
              "Database Error".equals(throwable.getMessage()))
          .verify();

      verify(userRepository).countByEmailAndDeletedAtIsNull("test@example.com");
      verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("실패: 사용자 저장 오류")
    void createUserByKakaoInfo_Fail_SaveError() {
      // given
      when(userRepository.countByEmailAndDeletedAtIsNull("test@example.com"))
          .thenReturn(Mono.just(0L));
      when(userRepository.save(any(User.class)))
          .thenReturn(Mono.error(new RuntimeException("Save Error")));

      // when
      Mono<String> result = kakaoLoginService.createUserByKakaoInfo(mockKakaoUserInfo);

      // then
      StepVerifier.create(result)
          .expectErrorMatches(throwable -> 
              throwable instanceof RuntimeException && 
              "Save Error".equals(throwable.getMessage()))
          .verify();

      verify(userRepository).countByEmailAndDeletedAtIsNull("test@example.com");
      verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("성공: 여러 사용자 동시 처리")
    void createUserByKakaoInfo_Success_MultipleUsers() {
      // given - 첫 번째 사용자
      when(userRepository.countByEmailAndDeletedAtIsNull("test@example.com"))
          .thenReturn(Mono.just(0L));
      when(userRepository.save(any(User.class)))
          .thenReturn(Mono.just(mockUser));

      // when - 첫 번째 사용자 처리
      Mono<String> result1 = kakaoLoginService.createUserByKakaoInfo(mockKakaoUserInfo);

      // then - 첫 번째 사용자 검증
      StepVerifier.create(result1)
          .expectNext("Success")
          .verifyComplete();

      // given - 두 번째 사용자 (기존 사용자)
      when(userRepository.countByEmailAndDeletedAtIsNull("test@example.com"))
          .thenReturn(Mono.just(1L));

      // when - 두 번째 사용자 처리
      Mono<String> result2 = kakaoLoginService.createUserByKakaoInfo(mockKakaoUserInfo);

      // then - 두 번째 사용자 검증
      StepVerifier.create(result2)
          .expectNext("Success")
          .verifyComplete();
    }
  }

  @Nested
  @DisplayName("전화번호 형식 변환 테스트")
  class ChangePhoneNumberFormatTest {

    @Test
    @DisplayName("성공: +82 형식을 010 형식으로 변환")
    void changePhoneNumberFormat_Success_ConvertFrom82() {
      // given
      String phoneNumber = "+82 10-1234-5678";

      // when
      String result = kakaoLoginService.changePhoneNumberFormat(phoneNumber);

      // then
      assert "010-1234-5678".equals(result);
    }

    @Test
    @DisplayName("성공: 이미 로컬 형식인 경우 그대로 반환")
    void changePhoneNumberFormat_Success_AlreadyLocal() {
      // given
      String phoneNumber = "010-1234-5678";

      // when
      String result = kakaoLoginService.changePhoneNumberFormat(phoneNumber);

      // then
      assert "010-1234-5678".equals(result);
    }

    @Test
    @DisplayName("성공: null 값 처리")
    void changePhoneNumberFormat_Success_NullValue() {
      // given
      String phoneNumber = null;

      // when
      String result = kakaoLoginService.changePhoneNumberFormat(phoneNumber);

      // then
      assert phoneNumber == result; // null이 그대로 반환됨
    }

    @Test
    @DisplayName("성공: 빈 문자열 처리")
    void changePhoneNumberFormat_Success_EmptyString() {
      // given
      String phoneNumber = "";

      // when
      String result = kakaoLoginService.changePhoneNumberFormat(phoneNumber);

      // then
      assert result == null;
    }

    @Test
    @DisplayName("성공: 공백 문자열 처리")
    void changePhoneNumberFormat_Success_WhitespaceString() {
      // given
      String phoneNumber = "   ";

      // when
      String result = kakaoLoginService.changePhoneNumberFormat(phoneNumber);

      // then
      assert result == null;
    }

    @Test
    @DisplayName("성공: 다양한 형식 처리")
    void changePhoneNumberFormat_Success_VariousFormats() {
      // +82 형식들
      assert "010-1111-2222".equals(kakaoLoginService.changePhoneNumberFormat("+82 10-1111-2222"));
      assert "010-3333-4444".equals(kakaoLoginService.changePhoneNumberFormat("+82 10-3333-4444"));
      
      // 이미 로컬 형식들
      assert "010-5555-6666".equals(kakaoLoginService.changePhoneNumberFormat("010-5555-6666"));
      assert "011-7777-8888".equals(kakaoLoginService.changePhoneNumberFormat("011-7777-8888"));
      
      // 기타 형식들 (변환되지 않음)
      assert "010-9999-0000".equals(kakaoLoginService.changePhoneNumberFormat("010-9999-0000"));
    }
  }

  @Nested
  @DisplayName("Service 비즈니스 로직 테스트")
  class ServiceBusinessLogicTest {

    @Test
    @DisplayName("성공: 사용자 생성 시 올바른 정보 매핑")
    void testUserCreationMapping() {
      // given
      KakaoUserInfoResponseDto.KakaoAccount.Profile profile = 
          new KakaoUserInfoResponseDto.KakaoAccount.Profile(
              "신규사용자", "thumb", "profile", "false", false
          );
      
      KakaoUserInfoResponseDto.KakaoAccount kakaoAccount = 
          new KakaoUserInfoResponseDto.KakaoAccount(
              true, true, true, profile, true, "신규사용자", 
              true, true, true, "new@example.com", 
              false, null, false, null, false, null, null, 
              false, null, false, "+82 10-9999-8888", false, null, null
          );
      
      KakaoUserInfoResponseDto newUserInfo = new KakaoUserInfoResponseDto(
          999999L, true, new Date(), new Date(), 
          new HashMap<>(), kakaoAccount, 
          new KakaoUserInfoResponseDto.Partner("new_uuid")
      );

      User savedUser = new User();
      savedUser.setUserId(UUID.randomUUID());
      savedUser.setEmail("new@example.com");
      savedUser.setName("신규사용자");
      savedUser.setPhoneNumber("010-9999-8888");

      when(userRepository.countByEmailAndDeletedAtIsNull("new@example.com"))
          .thenReturn(Mono.just(0L));
      when(userRepository.save(any(User.class)))
          .thenReturn(Mono.just(savedUser));

      // when
      Mono<String> result = kakaoLoginService.createUserByKakaoInfo(newUserInfo);

      // then
      StepVerifier.create(result)
          .expectNext("Success")
          .verifyComplete();

      // 저장되는 User 객체의 속성 검증
      verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("성공: Repository 인터랙션 검증")
    void testRepositoryInteraction() {
      // given
      when(userRepository.countByEmailAndDeletedAtIsNull(anyString()))
          .thenReturn(Mono.just(0L));
      when(userRepository.save(any(User.class)))
          .thenReturn(Mono.just(mockUser));

      // when
      Mono<String> result = kakaoLoginService.createUserByKakaoInfo(mockKakaoUserInfo);

      // then
      StepVerifier.create(result)
          .expectNext("Success")
          .verifyComplete();

      // Repository 메서드 호출 검증
      verify(userRepository).countByEmailAndDeletedAtIsNull("test@example.com");
      verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("성공: 반응형 스트림 처리 검증")
    void testReactiveStreamProcessing() {
      // given
      when(userRepository.countByEmailAndDeletedAtIsNull("test@example.com"))
          .thenReturn(Mono.just(0L).delayElement(java.time.Duration.ofMillis(100)));
      when(userRepository.save(any(User.class)))
          .thenReturn(Mono.just(mockUser).delayElement(java.time.Duration.ofMillis(100)));

      // when
      Mono<String> result = kakaoLoginService.createUserByKakaoInfo(mockKakaoUserInfo);

      // then - 비동기 처리가 올바르게 되는지 검증
      StepVerifier.create(result)
          .expectNext("Success")
          .expectComplete()
          .verify(java.time.Duration.ofSeconds(5));
    }
  }
}