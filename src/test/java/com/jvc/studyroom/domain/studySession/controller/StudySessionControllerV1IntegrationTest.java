package com.jvc.studyroom.domain.studySession.controller;

import com.jvc.studyroom.domain.studySession.dto.*;
import com.jvc.studyroom.domain.studySession.entity.SessionDuration;
import com.jvc.studyroom.domain.studySession.entity.SessionStatus;
import com.jvc.studyroom.domain.studySession.service.StudySessionService;
import com.jvc.studyroom.domain.user.model.User;
import com.jvc.studyroom.common.enums.AccountStatus;
import com.jvc.studyroom.common.enums.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

/**
 * StudySessionControllerV1의 통합 테스트
 * 간소화된 버전으로 Mock을 활용한 기본 동작 검증
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("StudySessionControllerV1 통합 테스트")
class StudySessionControllerV1IntegrationTest {

    private WebTestClient webTestClient;

    @Mock
    private StudySessionService studySessionService;

    private User testUser;
    private UUID testSessionId;

    @BeforeEach
    void setUp() {
        testUser = createTestUser();
        testSessionId = UUID.randomUUID();
        
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
        user.setUsername("testuser");
        user.setRole(UserRole.STUDENT);
        user.setAccountStatus(AccountStatus.ACTIVE);
        user.setCreatedAt(OffsetDateTime.now());
        user.setUpdatedAt(OffsetDateTime.now());
        return user;
    }

    @Test
    @DisplayName("전체 학습 세션 목록 조회 - 성공")
    void getSessionList_Success() {
        // given
        StudySessionListResponse response = new StudySessionListResponse("학생1", "A01");
        when(studySessionService.getSessionList(any(Sort.class)))
                .thenReturn(Flux.just(response));

        // when & then
        webTestClient.get()
                .uri("/study/sessions")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(StudySessionListResponse.class)
                .hasSize(1)
                .contains(response);
    }

    @Test
    @DisplayName("특정 세션 상세 조회 - 성공")
    void getSession_Success() {
        // given
        StudySessionResponse response = new StudySessionResponse("테스트 학생", "A01");
        when(studySessionService.getSession(testSessionId))
                .thenReturn(Mono.just(response));

        // when & then
        webTestClient.get()
                .uri("/study/sessions/{sessionId}", testSessionId)
                .exchange()
                .expectStatus().isOk()
                .expectBody(StudySessionResponse.class)
                .isEqualTo(response);
    }

    @Test
    @DisplayName("세션 생성 - 성공")
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
    }

    @Test
    @DisplayName("세션 상태 변경 - 성공")
    void changeSessionStatus_Success() {
        // given
        SessionChangeStatusRequest request = new SessionChangeStatusRequest(
                testSessionId, SessionStatus.PAUSED
        );
        
        when(studySessionService.changeSessionStatus(any(SessionChangeStatusRequest.class), any(User.class)))
                .thenReturn(Mono.empty());

        // when & then
        webTestClient.post()
                .uri("/study/sessions/change-status")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectBody().isEmpty();
    }

    @Test
    @DisplayName("로그인한 학생의 세션 히스토리 조회 - 성공")
    void getSessionHistoryByLoginedStudent_Success() {
        // given
        SessionHistoryResponse response = new SessionHistoryResponse(
                testSessionId, testUser.getUserId(), UUID.randomUUID(), 
                OffsetDateTime.now(), OffsetDateTime.now(), OffsetDateTime.now(),
                SessionStatus.COMPLETED, 120, 10, 1, null
        );
        
        when(studySessionService.getSessionHistory(any(UUID.class)))
                .thenReturn(Flux.just(response));

        // when & then
        webTestClient.get()
                .uri("/study/sessions/session-history")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(SessionHistoryResponse.class)
                .hasSize(1);
    }

    @Test
    @DisplayName("로그인한 학생의 현재 세션 조회 - 성공")
    void getCurrentSessionByStudent_Success() {
        // given
        SessionHistoryResponse response = new SessionHistoryResponse(
                testSessionId, testUser.getUserId(), UUID.randomUUID(), 
                OffsetDateTime.now(), null, OffsetDateTime.now(),
                SessionStatus.ACTIVE, 60, 5, 0, null
        );
        
        when(studySessionService.getCurrentSession(any(UUID.class)))
                .thenReturn(Flux.just(response));

        // when & then
        webTestClient.get()
                .uri("/study/sessions/current")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(SessionHistoryResponse.class)
                .hasSize(1);
    }

    @Test
    @DisplayName("관리자용 특정 학생 세션 히스토리 조회 - 성공")
    void getSessionHistoryFromAdmin_Success() {
        // given
        UUID studentId = UUID.randomUUID();
        SessionHistoryResponse response = new SessionHistoryResponse(
                testSessionId, studentId, UUID.randomUUID(), 
                OffsetDateTime.now(), OffsetDateTime.now(), OffsetDateTime.now(),
                SessionStatus.COMPLETED, 120, 10, 1, null
        );
        
        when(studySessionService.getSessionHistory(studentId))
                .thenReturn(Flux.just(response));

        // when & then
        webTestClient.get()
                .uri("/study/sessions/session-history/{studentId}", studentId)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(SessionHistoryResponse.class)
                .hasSize(1);
    }

    @Test
    @DisplayName("관리자용 특정 학생 현재 세션 조회 - 성공")
    void getCurrentSessionByStudentFromAdmin_Success() {
        // given
        UUID studentId = UUID.randomUUID();
        SessionHistoryResponse response = new SessionHistoryResponse(
                testSessionId, studentId, UUID.randomUUID(), 
                OffsetDateTime.now(), null, OffsetDateTime.now(),
                SessionStatus.ACTIVE, 60, 5, 0, null
        );
        
        when(studySessionService.getCurrentSession(studentId))
                .thenReturn(Flux.just(response));

        // when & then
        webTestClient.get()
                .uri("/study/sessions/current/{studentId}", studentId)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(SessionHistoryResponse.class)
                .hasSize(1);
    }
}
