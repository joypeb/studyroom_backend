package com.jvc.studyroom.domain.studySession.repository;

import com.jvc.studyroom.domain.studySession.entity.StudySession;
import org.springframework.data.domain.Sort;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface StudySessionRepository extends R2dbcRepository<StudySession, UUID> {
    Flux<StudySession> findAll();
    Mono<StudySession> findBySessionId(UUID sessionId);
        /*
    @Query("SELECT * FROM study_sessions WHERE session_id = :sessionId::uuid")
    Mono<StudySession> findStudySessionBySessionId(UUID sessionId);
         */

    //Mono<StudySession> saveStudySession(StudySession studySession);
    Flux<StudySession> findStudySessionByStudentId(UUID studentId);
}
