package com.jvc.studyroom.domain.monitoring.service;

import com.jvc.studyroom.domain.monitoring.dto.SessionMonitoringData;
import com.jvc.studyroom.domain.monitoring.dto.SessionSummaryData;
import com.jvc.studyroom.domain.studySession.entity.SessionStatus;
import com.jvc.studyroom.domain.studySession.entity.StudySession;
import com.jvc.studyroom.domain.studySession.repository.StudySessionRepository;
import com.jvc.studyroom.domain.user.model.User;
import com.jvc.studyroom.domain.user.repository.UserRepository;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * 스터디 세션 모니터링 서비스 - 세션 초기화 기준: 오전 6시 - 활성 세션(ACTIVE, PAUSED)만 조회
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MonitoringServiceV1 implements MonitoringService {

  private final StudySessionRepository studySessionRepository;
  private final UserRepository userRepository;

  /**
   * 당일 기준 활성화된 세션들을 스트림으로 반환 (ACTIVE, PAUSED 상태)
   */
  @Override
  public Flux<SessionMonitoringData> getAllTodaySessions() {
    OffsetDateTime[] todayBounds = calculateTodayBounds();
    OffsetDateTime startOfToday = todayBounds[0];
    OffsetDateTime endOfToday = todayBounds[1];

    log.info("당일 활성 세션 조회 범위 - 시작: {}, 종료: {}", startOfToday, endOfToday);

    return studySessionRepository
        .findByStartTimeBetween(startOfToday, endOfToday)
        .filter(this::isActiveSession)
        .collectList()
        .flatMapMany(this::buildSessionDataWithStudents);
  }

  /**
   * 세션이 활성화 상태인지 확인
   */
  private boolean isActiveSession(StudySession session) {
    return session.getSessionStatus() == SessionStatus.ACTIVE
        || session.getSessionStatus() == SessionStatus.PAUSED;
  }

  /**
   * 오전 6시 기준 당일 시간 범위 계산
   */
  private OffsetDateTime[] calculateTodayBounds() {
    OffsetDateTime now = OffsetDateTime.now();
    OffsetDateTime startOfToday;
    OffsetDateTime endOfToday;

    if (now.getHour() < 6) {
      // 현재 시간이 오전 6시 이전이면 전날 오전 6시부터 오늘 오전 6시까지
      startOfToday = now.minusDays(1).truncatedTo(ChronoUnit.DAYS).plusHours(6);
      endOfToday = now.truncatedTo(ChronoUnit.DAYS).plusHours(6);
    } else {
      // 현재 시간이 오전 6시 이후면 오늘 오전 6시부터 내일 오전 6시까지
      startOfToday = now.truncatedTo(ChronoUnit.DAYS).plusHours(6);
      endOfToday = now.plusDays(1).truncatedTo(ChronoUnit.DAYS).plusHours(6);
    }

    return new OffsetDateTime[]{startOfToday, endOfToday};
  }

  /**
   * 당일 활성 세션 목록 스냅샷 (요약)
   */
  @Override
  public Flux<SessionSummaryData> getTodaySessionsSummary() {
    OffsetDateTime[] todayBounds = calculateTodayBounds();
    OffsetDateTime startOfToday = todayBounds[0];
    OffsetDateTime endOfToday = todayBounds[1];

    log.info("당일 활성 세션 요약 조회 범위 - 시작: {}, 종료: {}", startOfToday, endOfToday);

    return studySessionRepository
        .findByStartTimeBetween(startOfToday, endOfToday)
        .filter(this::isActiveSession)
        .collectList()
        .flatMapMany(this::buildSessionSummaryWithStudents);
  }

  /**
   * 세션 요약 데이터와 학생 정보를 결합하여 스트림으로 반환
   */
  private Flux<SessionSummaryData> buildSessionSummaryWithStudents(
      java.util.List<StudySession> activeSessions) {

    if (activeSessions.isEmpty()) {
      log.info("활성화된 세션이 없습니다.");
      return Flux.empty();
    }

    // 학생 정보 조회
    Set<UUID> studentIds = activeSessions.stream()
        .map(StudySession::getStudentId)
        .collect(Collectors.toSet());

    Mono<Map<UUID, User>> studentMapMono = userRepository.findAllById(studentIds)
        .collectMap(User::getUserId)
        .defaultIfEmpty(Collections.emptyMap());

    return studentMapMono
        .flatMapMany(studentMap ->
            Flux.fromIterable(activeSessions)
                .map(session -> {
                  User student = studentMap.get(session.getStudentId());
                  return SessionSummaryData.from(session, student);
                })
        )
        .doOnNext(summaryData -> log.debug("세션 요약 데이터 생성: {}", summaryData.sessionId()))
        .doOnComplete(() -> log.info("총 {} 개의 활성 세션 요약 반환", activeSessions.size()));
  }

  private Flux<SessionMonitoringData> buildSessionDataWithStudents(
      java.util.List<StudySession> activeSessions) {

    if (activeSessions.isEmpty()) {
      log.info("활성화된 세션이 없습니다.");
      return Flux.empty();
    }

    // 학생 정보 조회
    Set<UUID> studentIds = activeSessions.stream()
        .map(StudySession::getStudentId)
        .collect(Collectors.toSet());

    Mono<Map<UUID, User>> studentMapMono = userRepository.findAllById(studentIds)
        .collectMap(User::getUserId)
        .defaultIfEmpty(Collections.emptyMap());

    return studentMapMono
        .flatMapMany(studentMap ->
            Flux.fromIterable(activeSessions)
                .map(session -> {
                  User student = studentMap.get(session.getStudentId());
                  return SessionMonitoringData.fromSessionOnly(session, student);
                })
        )
        .doOnNext(sessionData -> log.debug("세션 데이터 생성: {}", sessionData.sessionId()))
        .doOnComplete(() -> log.info("총 {} 개의 활성 세션 반환", activeSessions.size()));
  }
}
