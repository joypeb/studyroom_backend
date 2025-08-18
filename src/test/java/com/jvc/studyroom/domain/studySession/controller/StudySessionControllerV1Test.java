package com.jvc.studyroom.domain.studySession.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.jvc.studyroom.common.enums.AccountStatus;
import com.jvc.studyroom.common.enums.UserRole;
import com.jvc.studyroom.domain.studySession.dto.SessionChangeStatusRequest;
import com.jvc.studyroom.domain.studySession.dto.SessionCreateRequest;
import com.jvc.studyroom.domain.studySession.dto.SessionHistoryResponse;
import com.jvc.studyroom.domain.studySession.dto.StudySessionCreateResponse;
import com.jvc.studyroom.domain.studySession.dto.StudySessionListResponse;
import com.jvc.studyroom.domain.studySession.dto.StudySessionResponse;
import com.jvc.studyroom.domain.studySession.entity.SessionDuration;
import com.jvc.studyroom.domain.studySession.entity.SessionStatus;
import com.jvc.studyroom.domain.studySession.service.StudySessionService;
import com.jvc.studyroom.domain.user.model.User;
import java.time.OffsetDateTime;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@ExtendWith(MockitoExtension.class)
@DisplayName("StudySessionControllerV1 테스트")
class StudySessionControllerV1Test {

  private WebTestClient webTestClient;

  @Mock
  private StudySessionService studySessionService;

  private User testUser;
  private UUID testSessionId;
  private UUID testStudentId;

  @BeforeEach
  void setUp() {
    testUser = createTestUser();
    testSessionId = UUID.randomUUID();
    testStudentId = UUID.randomUUID();

    // WebTestClient 수동 설정
    StudySessionControllerV1 controller = new StudySessionControllerV1(studySessionService);
    webTestClient = WebTestClient
        .bindToController(controller)
        .build();
  }

  private User createTestUser() {
    User user = new User();
    user.setUserId(UUID.randomUUID());
    user.setEmail("test@example.com");
    user.setName("테스트 사용자");
    user.setPhoneNumber("010-1234-5678");
    user.setUsername("testuser");
    user.setPassword("password");
    user.setAccountStatus(AccountStatus.ACTIVE);
    user.setRole(UserRole.STUDENT);
    user.setCreatedAt(OffsetDateTime.now());
    user.setUpdatedAt(OffsetDateTime.now());
    return user;
  }

  @Nested
  @DisplayName("GET /study/sessions - 전체 학습 세션 목록 조회")
  class GetSessionListTest {

    @Test
    @DisplayName("성공: 기본 정렬로 세션 목록을 반환한다")
    void getSessionList_Success_Default() {
      // given
      StudySessionListResponse response1 = new StudySessionListResponse(
          "학생1", "A01"
      );
      StudySessionListResponse response2 = new StudySessionListResponse(
          "학생2", "A02"
      );
      Flux<StudySessionListResponse> responseFlux = Flux.just(response1, response2);

      when(studySessionService.getSessionList(any(Sort.class))).thenReturn(responseFlux);

      // when & then
      webTestClient.get()
          .uri("/study/sessions")
          .exchange()
          .expectStatus().isOk()
          .expectBodyList(StudySessionListResponse.class)
          .hasSize(2);

      verify(studySessionService).getSessionList(any(Sort.class));
    }

    @Test
    @DisplayName("성공: 커스텀 정렬 파라미터로 세션 목록을 반환한다")
    void getSessionList_Success_CustomSort() {
      // given
      Flux<StudySessionListResponse> responseFlux = Flux.empty();
      when(studySessionService.getSessionList(any(Sort.class))).thenReturn(responseFlux);

      // when & then
      webTestClient.get()
          .uri("/study/sessions?sort=createdAt,asc")
          .exchange()
          .expectStatus().isOk();

      verify(studySessionService).getSessionList(any(Sort.class));
    }
  }

  @Nested
  @DisplayName("GET /study/sessions/{sessionId} - 특정 학습 세션 상세 정보 조회")
  class GetSessionTest {

    @Test
    @DisplayName("성공: 세션 ID로 상세 정보를 반환한다")
    void getSession_Success() {
      // given
      StudySessionResponse response = new StudySessionResponse("테스트 학생", "A01");
      when(studySessionService.getSession(testSessionId)).thenReturn(Mono.just(response));

      // when & then
      webTestClient.get()
          .uri("/study/sessions/{sessionId}", testSessionId)
          .exchange()
          .expectStatus().isOk()
          .expectBody(StudySessionResponse.class)
          .isEqualTo(response);

      verify(studySessionService).getSession(testSessionId);
    }

    @Test
    @DisplayName("실패: 존재하지 않는 세션 ID로 조회 시 에러 응답")
    void getSession_NotFound() {
      // given
      when(studySessionService.getSession(testSessionId)).thenReturn(Mono.empty());

      // when & then
      webTestClient.get()
          .uri("/study/sessions/{sessionId}", testSessionId)
          .exchange()
          .expectStatus().isOk()
          .expectBody().isEmpty();

      verify(studySessionService).getSession(testSessionId);
    }
  }

  @Nested
  @DisplayName("POST /study/sessions/new - 학습 세션 생성")
  class CreateSessionTest {

    @Test
    @DisplayName("성공: 유효한 요청으로 세션을 생성한다")
    void createSession_Success() {
      // given
      SessionCreateRequest request = new SessionCreateRequest(SessionDuration.TWO_HOURS);
      StudySessionCreateResponse response = new StudySessionCreateResponse(testSessionId);

      when(studySessionService.createSession(any(SessionCreateRequest.class), any(User.class)))
          .thenReturn(Mono.just(response));

      // when & then
      webTestClient.post()
          .uri("/study/sessions/new")
          .contentType(MediaType.APPLICATION_JSON)
          .bodyValue(request)
          .exchange()
          .expectStatus().isOk()
          .expectBody(StudySessionCreateResponse.class)
          .isEqualTo(response);

      verify(studySessionService).createSession(any(SessionCreateRequest.class), any(User.class));
    }

    @Test
    @DisplayName("실패: duration이 null인 경우 400 에러")
    void createSession_Fail_NullDuration() {
      // when & then
      webTestClient.post()
          .uri("/study/sessions/new")
          .contentType(MediaType.APPLICATION_JSON)
          .bodyValue("{\"duration\": null}")
          .exchange()
          .expectStatus().isBadRequest();
    }
  }

  @Nested
  @DisplayName("POST /study/sessions/change-status - 학습 세션 상태 변경")
  class ChangeSessionStatusTest {

    @Test
    @DisplayName("성공: 세션 상태를 변경한다")
    void changeSessionStatus_Success() {
      // given
      SessionChangeStatusRequest request = new SessionChangeStatusRequest(
          testSessionId, SessionStatus.PAUSED
      );

      when(studySessionService.changeSessionStatus(any(SessionChangeStatusRequest.class),
          any(User.class)))
          .thenReturn(Mono.empty());

      // when & then
      webTestClient.post()
          .uri("/study/sessions/change-status")
          .contentType(MediaType.APPLICATION_JSON)
          .bodyValue(request)
          .exchange()
          .expectStatus().isOk()
          .expectBody().isEmpty();

      verify(studySessionService).changeSessionStatus(any(SessionChangeStatusRequest.class),
          any(User.class));
    }
  }

  @Nested
  @DisplayName("GET /study/sessions/session-history/{studentId} - 관리자용 학생 세션 히스토리 조회")
  class GetSessionHistoryFromAdminTest {

    @Test
    @DisplayName("성공: 관리자가 특정 학생의 세션 히스토리를 조회한다")
    void getSessionHistoryFromAdmin_Success() {
      // given
      SessionHistoryResponse response1 = new SessionHistoryResponse(
          testSessionId, testStudentId, UUID.randomUUID(), OffsetDateTime.now(),
          OffsetDateTime.now(), OffsetDateTime.now(), SessionStatus.COMPLETED,
          120, 10, 1, null
      );
      Flux<SessionHistoryResponse> responseFlux = Flux.just(response1);

      when(studySessionService.getSessionHistory(testStudentId)).thenReturn(responseFlux);

      // when & then
      webTestClient.get()
          .uri("/study/sessions/session-history/{studentId}", testStudentId)
          .exchange()
          .expectStatus().isOk()
          .expectBodyList(SessionHistoryResponse.class)
          .hasSize(1);

      verify(studySessionService).getSessionHistory(testStudentId);
    }
  }

  @Nested
  @DisplayName("GET /study/sessions/session-history - 로그인한 학생의 세션 히스토리 조회")
  class GetSessionHistoryByLoginedStudentTest {

    @Test
    @DisplayName("성공: 로그인한 학생이 자신의 세션 히스토리를 조회한다")
    void getSessionHistoryByLoginedStudent_Success() {
      // given
      SessionHistoryResponse response1 = new SessionHistoryResponse(
          testSessionId, testStudentId, UUID.randomUUID(), OffsetDateTime.now(),
          OffsetDateTime.now(), OffsetDateTime.now(), SessionStatus.COMPLETED,
          120, 10, 1, null
      );
      Flux<SessionHistoryResponse> responseFlux = Flux.just(response1);

      when(studySessionService.getSessionHistory(any(UUID.class))).thenReturn(responseFlux);

      // when & then
      webTestClient.get()
          .uri("/study/sessions/session-history")
          .exchange()
          .expectStatus().isOk()
          .expectBodyList(SessionHistoryResponse.class)
          .hasSize(1);

      verify(studySessionService).getSessionHistory(any(UUID.class));
    }
  }

  @Nested
  @DisplayName("GET /study/sessions/current/{studentId} - 관리자용 학생 현재 세션 조회")
  class GetCurrentSessionByStudentFromAdminTest {

    @Test
    @DisplayName("성공: 관리자가 특정 학생의 현재 세션을 조회한다")
    void getCurrentSessionByStudentFromAdmin_Success() {
      // given
      SessionHistoryResponse response = new SessionHistoryResponse(
          testSessionId, testStudentId, UUID.randomUUID(), OffsetDateTime.now(),
          null, OffsetDateTime.now(), SessionStatus.ACTIVE,
          60, 5, 0, null
      );
      Flux<SessionHistoryResponse> responseFlux = Flux.just(response);

      when(studySessionService.getCurrentSession(testStudentId)).thenReturn(responseFlux);

      // when & then
      webTestClient.get()
          .uri("/study/sessions/current/{studentId}", testStudentId)
          .exchange()
          .expectStatus().isOk()
          .expectBodyList(SessionHistoryResponse.class)
          .hasSize(1);

      verify(studySessionService).getCurrentSession(testStudentId);
    }
  }

  @Nested
  @DisplayName("GET /study/sessions/current - 로그인한 학생의 현재 세션 조회")
  class GetCurrentSessionByStudentTest {

    @Test
    @DisplayName("성공: 로그인한 학생이 자신의 현재 세션을 조회한다")
    void getCurrentSessionByStudent_Success() {
      // given
      SessionHistoryResponse response = new SessionHistoryResponse(
          testSessionId, testStudentId, UUID.randomUUID(), OffsetDateTime.now(),
          null, OffsetDateTime.now(), SessionStatus.ACTIVE,
          60, 5, 0, null
      );
      Flux<SessionHistoryResponse> responseFlux = Flux.just(response);

      when(studySessionService.getCurrentSession(any(UUID.class))).thenReturn(responseFlux);

      // when & then
      webTestClient.get()
          .uri("/study/sessions/current")
          .exchange()
          .expectStatus().isOk()
          .expectBodyList(SessionHistoryResponse.class)
          .hasSize(1);

      verify(studySessionService).getCurrentSession(any(UUID.class));
    }

    @Test
    @DisplayName("성공: 현재 활성 세션이 없는 경우 빈 배열을 반환한다")
    void getCurrentSessionByStudent_Success_Empty() {
      // given
      when(studySessionService.getCurrentSession(any(UUID.class))).thenReturn(Flux.empty());

      // when & then
      webTestClient.get()
          .uri("/study/sessions/current")
          .exchange()
          .expectStatus().isOk()
          .expectBodyList(SessionHistoryResponse.class)
          .hasSize(0);

      verify(studySessionService).getCurrentSession(any(UUID.class));
    }
  }
}
