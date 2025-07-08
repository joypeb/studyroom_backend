package com.jvc.studyroom.domain.studySession.service;

import com.jvc.studyroom.domain.studySession.dto.StudySessionListResponse;
import com.jvc.studyroom.domain.studySession.dto.StudySessionResponse;
import com.jvc.studyroom.domain.studySession.repository.StudySessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RequiredArgsConstructor
public class StudySessionServiceV1 implements StudySessionService{
    private final StudySessionRepository repository;

    @Override
    public Flux<StudySessionListResponse> getSessionList(Sort sort) {
        return repository.findAll(sort)
                // todo. Id로 가져오는것 name으로 변환해서 가져 와야 함
                .map(session -> new StudySessionListResponse(session.getStudentId(),session.getSessionId()));
    }
    @Override
    public Mono<StudySessionResponse> getSession(UUID sessionId) {
        return repository.findStudySessionBySessionId(sessionId)
                // todo. Id로 가져오는것 name으로 변환해서 가져 와야 함, 어떤 정보를 가져와야 할지 정하기
                .map(session-> new StudySessionResponse(session.getSessionId(), session.getSeatId()));
    }
}
