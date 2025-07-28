package com.jvc.studyroom.domain.studySession.repository;

import com.jvc.studyroom.domain.studySession.entity.SessionStatus;
import com.jvc.studyroom.domain.studySession.entity.StudySession;
import org.springframework.data.domain.Sort;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.UUID;

public interface StudySessionRepository extends R2dbcRepository<StudySession, UUID> {
    Flux<StudySession> findAll(Sort sort);
    Mono<StudySession> findBySessionId(UUID sessionId);
        /*
    @Query("SELECT * FROM study_sessions WHERE session_id = :sessionId::uuid")
    Mono<StudySession> findStudySessionBySessionId(UUID sessionId);
         */
    Flux<StudySession> findByStudentId(UUID studentId);

    Flux<StudySession> findAllByStudentIdAndSessionStatusIn(UUID studentId, Collection<SessionStatus> status);
}
