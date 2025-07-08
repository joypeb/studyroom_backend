package com.jvc.studyroom.domain.studySession.repository;

import com.jvc.studyroom.domain.studySession.entity.StudySession;
import org.springframework.data.domain.Sort;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface StudySessionRepository extends R2dbcRepository<StudySession, UUID> {
    Flux<StudySession> findAll(Sort sort);

    Mono<StudySession> findStudySessionBySessionId(UUID sessionId);


}
