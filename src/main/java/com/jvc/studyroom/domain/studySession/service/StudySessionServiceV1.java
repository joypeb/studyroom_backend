package com.jvc.studyroom.domain.studySession.service;

import com.jvc.studyroom.domain.seat.model.Seat;
import com.jvc.studyroom.domain.seat.service.SeatFindService;
import com.jvc.studyroom.domain.studySession.converter.SessionCreateMapper;
import com.jvc.studyroom.domain.studySession.dto.*;
import com.jvc.studyroom.domain.studySession.entity.SessionStatus;
import com.jvc.studyroom.domain.studySession.repository.StudySessionRepository;
import com.jvc.studyroom.domain.user.model.User;
import com.jvc.studyroom.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

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
                .map(seatId -> SessionCreateMapper.toEntity(
                        request,
                        new SessionCreateData(
                                loginUser.getUserId(),
                                seatId,
                                SessionStatus.READY,
                                1,
                                loginUser.getUserId()
                        )
                ))
                .flatMap(studySessionRepository::save)
                .map(savedSession -> new StudySessionCreateResponse(savedSession.getSessionId()));
    }

    @Override
    public Mono<Void> resumeSession(UUID sessionId) {
        return null;
    }
}
