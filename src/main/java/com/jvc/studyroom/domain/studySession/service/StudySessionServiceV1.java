package com.jvc.studyroom.domain.studySession.service;

import com.jvc.studyroom.common.utils.TimeUtil;
import com.jvc.studyroom.domain.seat.model.Seat;
import com.jvc.studyroom.domain.seat.service.SeatFindService;
import com.jvc.studyroom.domain.studySession.converter.SessionCreateMapper;
import com.jvc.studyroom.domain.studySession.converter.SessionFindMapper;
import com.jvc.studyroom.domain.studySession.dto.SessionChangeStatusRequest;
import com.jvc.studyroom.domain.studySession.dto.SessionCreateData;
import com.jvc.studyroom.domain.studySession.dto.SessionCreateRequest;
import com.jvc.studyroom.domain.studySession.dto.SessionHistoryResponse;
import com.jvc.studyroom.domain.studySession.dto.StudySessionCreateResponse;
import com.jvc.studyroom.domain.studySession.dto.StudySessionListResponse;
import com.jvc.studyroom.domain.studySession.dto.StudySessionResponse;
import com.jvc.studyroom.domain.studySession.entity.SessionStatus;
import com.jvc.studyroom.domain.studySession.entity.StudySession;
import com.jvc.studyroom.domain.studySession.repository.StudySessionRepository;
import com.jvc.studyroom.domain.user.model.User;
import com.jvc.studyroom.domain.user.repository.UserRepository;
import com.jvc.studyroom.exception.ErrorCode;
import com.jvc.studyroom.exception.customExceptions.StudyroomServiceException;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@RequiredArgsConstructor
public class StudySessionServiceV1 implements StudySessionService {

  private final StudySessionRepository studySessionRepository;
  private final UserRepository userRepository;
  private final SeatFindService seatfindService;

  @Override
  public Flux<StudySessionListResponse> getSessionList(Sort sort) {
    return studySessionRepository.findAll(sort)
        .flatMap(session ->
            userRepository.findByUserId(session.getStudentId())  // Mono<User>
                .zipWith(
                    seatfindService.findByAssignedStudentId(session.getStudentId())  // Mono<Seat>
                )
                .map(tuple -> {
                  User user = tuple.getT1();
                  com.jvc.studyroom.domain.seat.model.Seat seat = tuple.getT2();
                  return new StudySessionListResponse(
                      user.getName(),
                      seat.getSeatNumber()
                  );
                })
        );
  }

  @Override
  public Mono<StudySessionResponse> getSession(UUID sessionId) {
    return studySessionRepository.findBySessionId(sessionId)
        .flatMap(session ->
            userRepository.findByUserId(session.getStudentId())
                .zipWith(
                    seatfindService.findByAssignedStudentId(session.getStudentId())
                )
                .map(tuple -> {
                  User user = tuple.getT1();
                  Seat seat = tuple.getT2();
                  return new StudySessionResponse(
                      user.getName(),
                      seat.getSeatNumber()
                  );
                })
        );
  }

  @Override
  public Mono<StudySessionCreateResponse> createSession(SessionCreateRequest request,
      User loginUser) {
    return seatfindService.findSeatIdByAssignedStudentId(loginUser.getUserId())
        .switchIfEmpty(Mono.error(new StudyroomServiceException(
            ErrorCode.SEAT_NOT_ASSIGNED)))
        .map(seatId -> {
          OffsetDateTime startTime = TimeUtil.nowInKorea();
          StudySession entity = SessionCreateMapper.toEntity(
              request,
              new SessionCreateData(
                  loginUser.getUserId(),
                  seatId,
                  SessionStatus.ACTIVE,
                  1,
                  startTime,
                  loginUser.getUserId()
              )
          );
          log.debug("생성된 entity id :: {}", entity.getSessionId());
          //log.debug("[계획 종료 시간 계산 확인] startTime: {}, plannedEndTime: {}", entity.getStartTime(), entity.getPlannedEndTime());
          return entity;
        })
        .flatMap(studySessionRepository::save)
        .doOnError(error -> log.error("session save fail ::: {}", error))
        .doOnNext(savedSession -> log.info("session save success ::: savedSession.id ::: {} ",
            savedSession.getSessionId()))
        .map(savedSession -> new StudySessionCreateResponse(savedSession.getSessionId()));
  }

  @Override
  public Mono<Void> changeSessionStatus(SessionChangeStatusRequest request, User loginUser) {
    return studySessionRepository.findBySessionId(request.studySessionId())
        .switchIfEmpty(Mono.error(new StudyroomServiceException(
            ErrorCode.SESSION_BY_USER_NOT_EXIST)))
        .map(original -> updateSessionWithTimeTracking(original, request.sessionStatus()))
        .flatMap(studySessionRepository::save)
        .doOnError(error -> log.error("changeSessionStatus fail ::: {}", error))
        .doOnNext(updated -> log.info("changeSessionStatus success ::: {}", updated.toString()))
        .then();
  }

  /**
   * 세션 상태 변경 시 시간 추적 업데이트
   * todo. event 테이블 만들어서 관리한다면 해당 로직은 필요 없음
   */
  private StudySession updateSessionWithTimeTracking(StudySession original,
      SessionStatus newStatus) {
    OffsetDateTime now = TimeUtil.nowInKorea();
    SessionStatus oldStatus = original.getSessionStatus();

    log.info("세션 상태 변경: {} -> {} (세션: {})", oldStatus, newStatus, original.getSessionId());

    // 상태 변경에 따른 시간 계산
    StudySession.StudySessionBuilder builder = original.toBuilder()
        .sessionStatus(newStatus)
        .updatedAt(now);

    // 이전 상태가 ACTIVE였다면 공부 시간 누적
    if (oldStatus == SessionStatus.ACTIVE) {
      long additionalStudyMinutes = calculateTimeSinceLastUpdate(original, now);
      int newTotalStudyMinutes = original.getTotalStudyMinutes() + (int) additionalStudyMinutes;
      builder.totalStudyMinutes(newTotalStudyMinutes);
      log.info("공부 시간 추가: {}분 (총: {}분)", additionalStudyMinutes, newTotalStudyMinutes);
    }

    // 이전 상태가 PAUSED였다면 휴식 시간 누적  
    if (oldStatus == SessionStatus.PAUSED) {
      long additionalBreakMinutes = calculateTimeSinceLastUpdate(original, now);
      int newTotalBreakMinutes = original.getTotalBreakMinutes() + (int) additionalBreakMinutes;
      builder.totalBreakMinutes(newTotalBreakMinutes);
      log.info("휴식 시간 추가: {}분 (총: {}분)", additionalBreakMinutes, newTotalBreakMinutes);
    }

    // PAUSED로 변경 시 pause 카운트 증가
    if (newStatus == SessionStatus.PAUSED && oldStatus != SessionStatus.PAUSED) {
      builder.pauseCount(original.getPauseCount() + 1);
      log.info("일시정지 횟수 증가: {}", original.getPauseCount() + 1);
    }

    // 세션 종료 시 endTime 설정
    if (newStatus == SessionStatus.COMPLETED || newStatus == SessionStatus.INTERRUPTED) {
      builder.endTime(now);
      log.info("세션 종료 시간 설정: {}", now);
    }

    return builder.build();
  }

  /**
   * 마지막 업데이트 이후 경과 시간 계산 (분)
   */
  private long calculateTimeSinceLastUpdate(StudySession session, OffsetDateTime now) {
    OffsetDateTime lastUpdate = session.getUpdatedAt() != null ?
        session.getUpdatedAt() : session.getCreatedAt();

    if (lastUpdate == null) {
      lastUpdate = session.getStartTime();
    }

    return java.time.temporal.ChronoUnit.MINUTES.between(lastUpdate, now);
  }

  @Override
  public Flux<SessionHistoryResponse> getSessionHistory(UUID studentId) {
    return studySessionRepository.findByStudentId(studentId)
        .switchIfEmpty(Mono.fromRunnable(() ->
            log.error("not exist findSessionByStudentId  :: studentId :: {}", studentId)
        ))
        .map(SessionFindMapper::toSessionHistoryResponse)
        .doOnError(error -> log.error("getSessionHistory fail ::: {}", error))
        ;
  }

  @Override
  public Flux<SessionHistoryResponse> getCurrentSession(UUID studentId) {
    List<SessionStatus> currentStatus = List.of(
        SessionStatus.ACTIVE,
        SessionStatus.PAUSED,
        SessionStatus.COMPLETED
    );
    return studySessionRepository.findAllByStudentIdAndSessionStatusIn(studentId, currentStatus)
        .switchIfEmpty(Mono.fromRunnable(() ->
            log.error(
                "not exist findAllByStudentIdAndSessionStatusIn  :: studentId :: {} :: currentStatus :: {}",
                studentId, currentStatus.toString())
        ))
        .map(SessionFindMapper::toSessionHistoryResponse)
        .doOnError(error -> log.error("getCurrentSession fail ::: {}", error))
        ;
  }
}
