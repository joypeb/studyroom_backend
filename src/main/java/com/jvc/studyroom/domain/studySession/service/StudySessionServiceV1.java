package com.jvc.studyroom.domain.studySession.service;

import com.jvc.studyroom.domain.seat.model.Seat;
import com.jvc.studyroom.domain.seat.service.SeatFindService;
import com.jvc.studyroom.domain.studySession.converter.SessionCreateMapper;
import com.jvc.studyroom.domain.studySession.converter.SessionFindMapper;
import com.jvc.studyroom.domain.studySession.dto.*;
import com.jvc.studyroom.domain.studySession.entity.SessionStatus;
import com.jvc.studyroom.domain.studySession.entity.StudySession;
import com.jvc.studyroom.domain.studySession.repository.StudySessionRepository;
import com.jvc.studyroom.domain.user.model.User;
import com.jvc.studyroom.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
@Slf4j
@RequiredArgsConstructor
public class StudySessionServiceV1 implements StudySessionService{
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
    public Mono<StudySessionCreateResponse> createSession(SessionCreateRequest request, User loginUser) {
        return seatfindService.findSeatIdByAssignedStudentId(loginUser.getUserId())
                .switchIfEmpty(Mono.fromRunnable(() ->
                                log.error("not exist seatIdByAssignedStudent  :: studentId :: {}", loginUser.getUserId())
                        ))
                .map(seatId -> {
                    StudySession entity = SessionCreateMapper.toEntity(
                            request,
                            new SessionCreateData(
                                    loginUser.getUserId(),
                                    seatId,
                                    SessionStatus.ACTIVE,
                                    1,
                                    OffsetDateTime.now(),
                                    loginUser.getUserId()
                            )
                    );
                    log.info("생성된 entity :: {}", entity);
                    return entity;
                })
                .flatMap(studySessionRepository::save)
                .doOnError(error -> log.error("session save fail ::: {}", error))
                .doOnNext(saved-> log.info("session save success ::: entity ::: {} ",saved.toString()))
                .map(savedSession -> new StudySessionCreateResponse(savedSession.getSessionId()));
    }

    @Override
    public Mono<Void> changeSessionStatus(SessionChangeStatusRequest request, User loginUser) {
        return studySessionRepository.findBySessionId(request.studySessionId())
            .switchIfEmpty(Mono.fromRunnable(() ->
                    log.error("not exist findSessionBySessionId  :: studySessionId :: {}", request.studySessionId())
                 ))
            .map(original -> original.toBuilder()
                    .sessionStatus(request.sessionStatus())
                    .build()
            )
            .flatMap(studySessionRepository::save)
            .doOnError(error -> log.error("changeSessionStatus fail ::: {}", error))
            .doOnNext(updated -> log.info("changeSessionStatus success ::: {}", updated.toString()))
            .then()
                ;
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
                        log.error("not exist findAllByStudentIdAndSessionStatusIn  :: studentId :: {} :: currentStatus :: {}", studentId, currentStatus.toString())
                ))
                .map(SessionFindMapper::toSessionHistoryResponse)
                .doOnError(error -> log.error("getCurrentSession fail ::: {}", error))
                ;
    }
}
