package com.jvc.studyroom.domain.studySession.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.jvc.studyroom.common.enums.AccountStatus;
import com.jvc.studyroom.common.enums.UserRole;
import com.jvc.studyroom.domain.seat.model.Seat;
import com.jvc.studyroom.domain.seat.service.SeatFindService;
import com.jvc.studyroom.domain.studySession.dto.SessionChangeStatusRequest;
import com.jvc.studyroom.domain.studySession.dto.SessionCreateRequest;
import com.jvc.studyroom.domain.studySession.dto.SessionHistoryResponse;
import com.jvc.studyroom.domain.studySession.dto.StudySessionCreateResponse;
import com.jvc.studyroom.domain.studySession.dto.StudySessionListResponse;
import com.jvc.studyroom.domain.studySession.dto.StudySessionResponse;
import com.jvc.studyroom.domain.studySession.entity.SessionDuration;
import com.jvc.studyroom.domain.studySession.entity.SessionStatus;
import com.jvc.studyroom.domain.studySession.entity.StudySession;
import com.jvc.studyroom.domain.studySession.repository.StudySessionRepository;
import com.jvc.studyroom.domain.studySession.service.StudySessionTimeCalculator;
import com.jvc.studyroom.domain.user.model.User;
import com.jvc.studyroom.domain.user.repository.UserRepository;
import com.jvc.studyroom.exception.customExceptions.StudyroomServiceException;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
@DisplayName("StudySessionServiceV1 테스트")
class StudySessionServiceV1Test {

  @Mock
  private StudySessionRepository studySessionRepository;

  @Mock
  private UserRepository userRepository;

  @Mock
  private SeatFindService seatFindService;

  @Mock
  private StudySessionTimeCalculator timeCalculator;

  @InjectMocks
  private StudySessionServiceV1 studySessionService;

  private User testUser;
  private StudySession testSession;
  private Seat testSeat;
  private UUID testSessionId;
  private UUID testSeatId;

  @BeforeEach
  void setUp() {
    testUser = createTestUser();
    testSessionId = UUID.randomUUID();
    testSeatId = UUID.randomUUID();
    testSeat = createTestSeat();
    testSession = createTestSession();
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
    user.setAssignedSeatId(testSeatId);
    user.setCreatedAt(OffsetDateTime.now());
    user.setUpdatedAt(OffsetDateTime.now());
    return user;
  }

  private Seat createTestSeat() {
    return Seat.builder()
        .seatId(testSeatId)
        .seatNumber("A01")
        .roomName("스터디룸1")
        .assignedStudentId(testUser.getUserId())
        .seatStatus(com.jvc.studyroom.common.enums.SeatStatus.OCCUPIED)
        .hasPowerOutlet(true)
        .hasDeskLamp(true)
        .hasLocker(true)
        .isNearWindow(false)
        .isActive(true)
        .createdAt(OffsetDateTime.now())
        .updatedAt(OffsetDateTime.now())
        .build();
  }

  private StudySession createTestSession() {
    return StudySession.builder()
        .sessionId(testSessionId)
        .studentId(testUser.getUserId())
        .seatId(testSeatId)
        .startTime(OffsetDateTime.now())
        .plannedEndTime(OffsetDateTime.now().plusHours(2))
        .sessionStatus(SessionStatus.READY)
        .totalStudyMinutes(0)
        .totalBreakMinutes(0)
        .pauseCount(0)
        .version(1)
        .createdAt(OffsetDateTime.now())
        .updatedAt(OffsetDateTime.now())
        .createdBy(testUser.getUserId())
        .build();
  }

  @Nested
  @DisplayName("getSessionList - 세션 목록 조회")
  class GetSessionListTest {

    @Test
    @DisplayName("성공: 정렬된 세션 목록을 반환한다")
    void getSessionList_Success() {
      // given
      Sort sort = Sort.by(Sort.Order.desc("createdAt"));
      StudySession session1 = createTestSession();
      StudySession session2 = createTestSession().toBuilder()
          .sessionId(UUID.randomUUID())
          .studentId(UUID.randomUUID())
          .sessionStatus(SessionStatus.ACTIVE)
          .build();

      User user2 = createTestUser();
      user2.setUserId(session2.getStudentId());
      user2.setName("테스트 사용자2");

      Seat seat2 = Seat.builder()
          .seatId(UUID.randomUUID())
          .seatNumber("A02")
          .roomName("스터디룸1")
          .assignedStudentId(user2.getUserId())
          .seatStatus(com.jvc.studyroom.common.enums.SeatStatus.OCCUPIED)
          .hasPowerOutlet(true)
          .hasDeskLamp(true)
          .hasLocker(true)
          .isNearWindow(false)
          .isActive(true)
          .createdAt(OffsetDateTime.now())
          .updatedAt(OffsetDateTime.now())
          .build();

      when(studySessionRepository.findAll(sort))
          .thenReturn(Flux.just(session1, session2));
      when(userRepository.findByUserId(session1.getStudentId()))
          .thenReturn(Mono.just(testUser));
      when(userRepository.findByUserId(session2.getStudentId()))
          .thenReturn(Mono.just(user2));
      when(seatFindService.findByAssignedStudentId(session1.getStudentId()))
          .thenReturn(Mono.just(testSeat));
      when(seatFindService.findByAssignedStudentId(session2.getStudentId()))
          .thenReturn(Mono.just(seat2));

      // when
      Flux<StudySessionListResponse> result = studySessionService.getSessionList(sort);

      // then
      StepVerifier.create(result)
          .expectNextMatches(response ->
              response.studentName().equals("테스트 사용자") &&
                  response.seatNumber().equals("A01")
          )
          .expectNextMatches(response ->
              response.studentName().equals("테스트 사용자2") &&
                  response.seatNumber().equals("A02")
          )
          .verifyComplete();

      verify(studySessionRepository).findAll(sort);
      verify(userRepository, times(2)).findByUserId(any(UUID.class));
      verify(seatFindService, times(2)).findByAssignedStudentId(any(UUID.class));
    }

    @Test
    @DisplayName("성공: 빈 목록을 반환한다")
    void getSessionList_Success_Empty() {
      // given
      Sort sort = Sort.by(Sort.Order.desc("createdAt"));
      when(studySessionRepository.findAll(sort))
          .thenReturn(Flux.empty());

      // when
      Flux<StudySessionListResponse> result = studySessionService.getSessionList(sort);

      // then
      StepVerifier.create(result)
          .verifyComplete();

      verify(studySessionRepository).findAll(sort);
    }
  }

  @Nested
  @DisplayName("getSession - 특정 세션 조회")
  class GetSessionTest {

    @Test
    @DisplayName("성공: 세션 ID로 세션 정보를 반환한다")
    void getSession_Success() {
      // given
      when(studySessionRepository.findBySessionId(testSessionId))
          .thenReturn(Mono.just(testSession));
      when(userRepository.findByUserId(testUser.getUserId()))
          .thenReturn(Mono.just(testUser));
      when(seatFindService.findByAssignedStudentId(testUser.getUserId()))
          .thenReturn(Mono.just(testSeat));

      // when
      Mono<StudySessionResponse> result = studySessionService.getSession(testSessionId);

      // then
      StepVerifier.create(result)
          .expectNextMatches(response ->
              response.studentName().equals("테스트 사용자") &&
                  response.seatNumber().equals("A01")
          )
          .verifyComplete();

      verify(studySessionRepository).findBySessionId(testSessionId);
      verify(userRepository).findByUserId(testUser.getUserId());
      verify(seatFindService).findByAssignedStudentId(testUser.getUserId());
    }

    @Test
    @DisplayName("실패: 존재하지 않는 세션 ID")
    void getSession_NotFound() {
      // given
      when(studySessionRepository.findBySessionId(testSessionId))
          .thenReturn(Mono.empty());

      // when
      Mono<StudySessionResponse> result = studySessionService.getSession(testSessionId);

      // then
      StepVerifier.create(result)
          .verifyComplete();

      verify(studySessionRepository).findBySessionId(testSessionId);
    }
  }

  @Nested
  @DisplayName("createSession - 세션 생성")
  class CreateSessionTest {

    @Test
    @DisplayName("성공: 새로운 세션을 생성한다")
    void createSession_Success() {
      // given
      SessionCreateRequest request = new SessionCreateRequest(SessionDuration.TWO_HOURS);
      StudySession savedSession = testSession.toBuilder()
          .sessionStatus(SessionStatus.ACTIVE)
          .build();

      when(seatFindService.findSeatIdByAssignedStudentId(testUser.getUserId()))
          .thenReturn(Mono.just(testSeatId));
      when(studySessionRepository.save(any(StudySession.class)))
          .thenReturn(Mono.just(savedSession));

      // when
      Mono<StudySessionCreateResponse> result = studySessionService.createSession(request,
          testUser);

      // then
      StepVerifier.create(result)
          .expectNextMatches(response ->
              response.sessionId().equals(testSessionId)
          )
          .verifyComplete();

      verify(seatFindService).findSeatIdByAssignedStudentId(testUser.getUserId());
      verify(studySessionRepository).save(any(StudySession.class));
    }

    @Test
    @DisplayName("실패: 할당된 좌석이 없는 경우")
    void createSession_Fail_NoAssignedSeat() {
      // given
      SessionCreateRequest request = new SessionCreateRequest(SessionDuration.TWO_HOURS);
      when(seatFindService.findSeatIdByAssignedStudentId(testUser.getUserId()))
          .thenReturn(Mono.empty());

      // when
      Mono<StudySessionCreateResponse> result = studySessionService.createSession(request,
          testUser);

      // then
      StepVerifier.create(result)
          .expectError(StudyroomServiceException.class)
          .verify();

      verify(seatFindService).findSeatIdByAssignedStudentId(testUser.getUserId());
      verify(studySessionRepository, never()).save(any(StudySession.class));
    }
  }

  @Nested
  @DisplayName("changeSessionStatus - 세션 상태 변경")
  class ChangeSessionStatusTest {

    @Test
    @DisplayName("성공: 세션 상태를 READY에서 ACTIVE로 변경한다")
    void changeSessionStatus_ReadyToActive_Success() {
      // given
      SessionChangeStatusRequest request = new SessionChangeStatusRequest(
          testSessionId, SessionStatus.ACTIVE
      );

      StudySession existingSession = testSession.toBuilder()
          .sessionStatus(SessionStatus.READY)
          .build();

      StudySession updatedSession = existingSession.toBuilder()
          .sessionStatus(SessionStatus.ACTIVE)
          .build();

      StudySessionTimeCalculator.StudySessionTimeUpdate timeUpdate = 
          new StudySessionTimeCalculator.StudySessionTimeUpdate(0, 0, false);

      when(studySessionRepository.findBySessionId(testSessionId))
          .thenReturn(Mono.just(existingSession));
      when(timeCalculator.calculateTimeUpdateForStatusChange(any(StudySession.class), any(SessionStatus.class), any(OffsetDateTime.class)))
          .thenReturn(timeUpdate);
      when(studySessionRepository.save(any(StudySession.class)))
          .thenReturn(Mono.just(updatedSession));

      // when
      Mono<Void> result = studySessionService.changeSessionStatus(request, testUser);

      // then
      StepVerifier.create(result)
          .verifyComplete();

      verify(studySessionRepository).findBySessionId(testSessionId);
      verify(timeCalculator).calculateTimeUpdateForStatusChange(any(StudySession.class), any(SessionStatus.class), any(OffsetDateTime.class));
      verify(studySessionRepository).save(any(StudySession.class));
    }

    @Test
    @DisplayName("성공: 세션 상태를 ACTIVE에서 PAUSED로 변경하고 공부 시간을 누적한다")
    void changeSessionStatus_ActiveToPaused_Success() {
      // given
      SessionChangeStatusRequest request = new SessionChangeStatusRequest(
          testSessionId, SessionStatus.PAUSED
      );

      StudySession existingSession = testSession.toBuilder()
          .sessionStatus(SessionStatus.ACTIVE)
          .totalStudyMinutes(30)  // 기존 30분
          .pauseCount(0)
          .updatedAt(OffsetDateTime.now().minusMinutes(10)) // 10분 전 업데이트
          .build();

      StudySessionTimeCalculator.StudySessionTimeUpdate timeUpdate = 
          new StudySessionTimeCalculator.StudySessionTimeUpdate(10, 0, true);

      when(studySessionRepository.findBySessionId(testSessionId))
          .thenReturn(Mono.just(existingSession));
      when(timeCalculator.calculateTimeUpdateForStatusChange(any(StudySession.class), any(SessionStatus.class), any(OffsetDateTime.class)))
          .thenReturn(timeUpdate);
      when(studySessionRepository.save(any(StudySession.class)))
          .thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

      // when
      Mono<Void> result = studySessionService.changeSessionStatus(request, testUser);

      // then
      StepVerifier.create(result)
          .verifyComplete();

      verify(studySessionRepository).findBySessionId(testSessionId);
      verify(timeCalculator).calculateTimeUpdateForStatusChange(any(StudySession.class), any(SessionStatus.class), any(OffsetDateTime.class));
      verify(studySessionRepository).save(argThat(session ->
          session.getSessionStatus() == SessionStatus.PAUSED &&
              session.getPauseCount() == 1 &&
              session.getTotalStudyMinutes() == 40  // 30분 + 10분 = 40분 확인
      ));
    }

    @Test
    @DisplayName("성공: 세션 상태를 PAUSED에서 ACTIVE로 변경하고 휴식 시간을 누적한다")
    void changeSessionStatus_PausedToActive_Success() {
      // given
      SessionChangeStatusRequest request = new SessionChangeStatusRequest(
          testSessionId, SessionStatus.ACTIVE
      );

      StudySession existingSession = testSession.toBuilder()
          .sessionStatus(SessionStatus.PAUSED)
          .totalBreakMinutes(15)  // 기존 15분
          .updatedAt(OffsetDateTime.now().minusMinutes(5)) // 5분 전 업데이트
          .build();

      StudySessionTimeCalculator.StudySessionTimeUpdate timeUpdate = 
          new StudySessionTimeCalculator.StudySessionTimeUpdate(0, 5, false);

      when(studySessionRepository.findBySessionId(testSessionId))
          .thenReturn(Mono.just(existingSession));
      when(timeCalculator.calculateTimeUpdateForStatusChange(any(StudySession.class), any(SessionStatus.class), any(OffsetDateTime.class)))
          .thenReturn(timeUpdate);
      when(studySessionRepository.save(any(StudySession.class)))
          .thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

      // when
      Mono<Void> result = studySessionService.changeSessionStatus(request, testUser);

      // then
      StepVerifier.create(result)
          .verifyComplete();

      verify(studySessionRepository).save(argThat(session ->
          session.getSessionStatus() == SessionStatus.ACTIVE &&
              session.getTotalBreakMinutes() == 20  // 15분 + 5분 = 20분 확인
      ));
    }

    @Test
    @DisplayName("실패: 존재하지 않는 세션")
    void changeSessionStatus_SessionNotFound() {
      // given
      SessionChangeStatusRequest request = new SessionChangeStatusRequest(
          testSessionId, SessionStatus.ACTIVE
      );

      when(studySessionRepository.findBySessionId(testSessionId))
          .thenReturn(Mono.empty());

      // when
      Mono<Void> result = studySessionService.changeSessionStatus(request, testUser);

      // then
      StepVerifier.create(result)
          .expectError(StudyroomServiceException.class)
          .verify();

      verify(studySessionRepository).findBySessionId(testSessionId);
      verify(studySessionRepository, never()).save(any(StudySession.class));
    }
  }

  @Nested
  @DisplayName("getSessionHistory - 세션 히스토리 조회")
  class GetSessionHistoryTest {

    @Test
    @DisplayName("성공: 학생의 세션 히스토리를 반환한다")
    void getSessionHistory_Success() {
      // given
      UUID studentId = testUser.getUserId();
      StudySession completedSession = testSession.toBuilder()
          .sessionStatus(SessionStatus.COMPLETED)
          .endTime(OffsetDateTime.now())
          .totalStudyMinutes(120)
          .build();

      when(studySessionRepository.findByStudentId(studentId))
          .thenReturn(Flux.just(completedSession));

      // when
      Flux<SessionHistoryResponse> result = studySessionService.getSessionHistory(studentId);

      // then
      StepVerifier.create(result)
          .expectNextMatches(response ->
              response.sessionId().equals(testSessionId) &&
                  response.studentId().equals(studentId) &&
                  response.sessionStatus().equals(SessionStatus.COMPLETED) &&
                  response.totalStudyMinutes() == 120
          )
          .verifyComplete();

      verify(studySessionRepository).findByStudentId(studentId);
    }

    @Test
    @DisplayName("성공: 세션 히스토리가 없는 경우 빈 결과 반환")
    void getSessionHistory_Success_Empty() {
      // given
      UUID studentId = testUser.getUserId();
      when(studySessionRepository.findByStudentId(studentId))
          .thenReturn(Flux.empty());

      // when
      Flux<SessionHistoryResponse> result = studySessionService.getSessionHistory(studentId);

      // then
      StepVerifier.create(result)
          .verifyComplete();

      verify(studySessionRepository).findByStudentId(studentId);
    }
  }

  @Nested
  @DisplayName("getCurrentSession - 현재 세션 조회")
  class GetCurrentSessionTest {

    @Test
    @DisplayName("성공: 현재 활성 세션을 반환한다")
    void getCurrentSession_Success() {
      // given
      UUID studentId = testUser.getUserId();
      StudySession activeSession = testSession.toBuilder()
          .sessionStatus(SessionStatus.ACTIVE)
          .endTime(null)
          .totalStudyMinutes(60)
          .build();

      List<SessionStatus> currentStatuses = List.of(
          SessionStatus.ACTIVE, SessionStatus.PAUSED, SessionStatus.COMPLETED
      );

      when(studySessionRepository.findAllByStudentIdAndSessionStatusIn(studentId, currentStatuses))
          .thenReturn(Flux.just(activeSession));

      // when
      Flux<SessionHistoryResponse> result = studySessionService.getCurrentSession(studentId);

      // then
      StepVerifier.create(result)
          .expectNextMatches(response ->
              response.sessionId().equals(testSessionId) &&
                  response.studentId().equals(studentId) &&
                  response.sessionStatus().equals(SessionStatus.ACTIVE) &&
                  response.endTime() == null &&
                  response.totalStudyMinutes() == 60
          )
          .verifyComplete();

      verify(studySessionRepository).findAllByStudentIdAndSessionStatusIn(studentId,
          currentStatuses);
    }

    @Test
    @DisplayName("성공: 현재 활성 세션이 없는 경우 빈 결과 반환")
    void getCurrentSession_Success_Empty() {
      // given
      UUID studentId = testUser.getUserId();
      List<SessionStatus> currentStatuses = List.of(
          SessionStatus.ACTIVE, SessionStatus.PAUSED, SessionStatus.COMPLETED
      );

      when(studySessionRepository.findAllByStudentIdAndSessionStatusIn(studentId, currentStatuses))
          .thenReturn(Flux.empty());

      // when
      Flux<SessionHistoryResponse> result = studySessionService.getCurrentSession(studentId);

      // then
      StepVerifier.create(result)
          .verifyComplete();

      verify(studySessionRepository).findAllByStudentIdAndSessionStatusIn(studentId,
          currentStatuses);
    }
  }
}
