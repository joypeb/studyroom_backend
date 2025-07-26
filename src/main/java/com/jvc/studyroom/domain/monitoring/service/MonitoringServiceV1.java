package com.jvc.studyroom.domain.monitoring.service;

import com.jvc.studyroom.domain.monitoring.dto.SessionMonitoringData;
import com.jvc.studyroom.domain.monitoring.dto.SessionStatusChangeEvent;
import com.jvc.studyroom.domain.monitoring.dto.StudySessionMonitoringResponse;
import com.jvc.studyroom.domain.seat.model.Seat;
import com.jvc.studyroom.domain.seat.repository.SeatRepository;
import com.jvc.studyroom.domain.studySession.entity.SessionStatus;
import com.jvc.studyroom.domain.studySession.entity.StudySession;
import com.jvc.studyroom.domain.studySession.repository.StudySessionRepository;
import com.jvc.studyroom.domain.user.model.User;
import com.jvc.studyroom.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 스터디 세션 모니터링 서비스
 * 학생-좌석 1:1 매핑 기반 세션 모니터링
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MonitoringServiceV1 implements MonitoringService {
    private final SeatRepository seatRepository;
    private final StudySessionRepository studySessionRepository;
    private final UserRepository userRepository;

    // 실시간 이벤트 스트림
    private final Sinks.Many<SessionStatusChangeEvent> sessionEventSink =
            Sinks.many().multicast().onBackpressureBuffer();

    // 연장 요청 관리 todo. DB같은 외부 저장소 사용 으로 변경
    //private final Map<UUID, SessionExtensionRequest> pendingExtensions = new HashMap<>();

    /**
     * 전체 활성 세션 모니터링 현황
     */
    @Override
    public Mono<StudySessionMonitoringResponse> getAllActiveSessionsStatus() {
        return studySessionRepository.findAllBySessionStatusIn(
                        List.of(SessionStatus.ACTIVE, SessionStatus.PAUSED))
                .collectList()
                .flatMap(this::buildMonitoringResponse)
                .doOnSuccess(response -> log.debug("활성 세션 {} 개 조회 완료", response.totalActiveSessions()))
                .onErrorResume(error -> {
                    log.error("활성 세션 현황 조회 오류: ", error);
                    return Mono.just(StudySessionMonitoringResponse.empty());
                });
    }




    /**
     * 모니터링 응답 구성
     */
    private Mono<StudySessionMonitoringResponse> buildMonitoringResponse(List<StudySession> sessions) {
        if (sessions.isEmpty()) {
            return Mono.just(StudySessionMonitoringResponse.empty());
        }

        // 학생 정보 조회
        Set<UUID> studentIds = sessions.stream()
                .map(StudySession::getStudentId)
                .collect(Collectors.toSet());

        Mono<Map<UUID, User>> studentMapMono = userRepository.findAllById(studentIds)
                .collectMap(User::getUserId);

        // 좌석 정보 조회
        Set<UUID> seatIds = sessions.stream()
                .map(StudySession::getSeatId)
                .collect(Collectors.toSet());

        Mono<Map<UUID, Seat>> seatMapMono = seatRepository.findAllById(seatIds)
                .collectMap(Seat::getSeatId);

        return Mono.zip(studentMapMono, seatMapMono)
                .map(tuple -> {
                    Map<UUID, User> studentMap = tuple.getT1();
                    Map<UUID, Seat> seatMap = tuple.getT2();

                    List<SessionMonitoringData> sessionData = sessions.stream()
                            .map(session -> SessionMonitoringData.from(
                                    session,
                                    studentMap.get(session.getStudentId()),
                                    seatMap.get(session.getSeatId())
                            ))
                            .collect(Collectors.toList());

                    Map<String, List<SessionMonitoringData>> groupSessions = sessionData.stream()
                            .collect(Collectors.groupingBy(SessionMonitoringData::groupName));

                    return new StudySessionMonitoringResponse(
                            OffsetDateTime.now(),
                            sessionData,
                            groupSessions,
                            sessionData.size(),
                            (int) sessionData.stream().map(SessionMonitoringData::studentId).distinct().count()
                    );
                });
    }
}
