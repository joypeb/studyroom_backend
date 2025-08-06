package com.jvc.studyroom.domain.studySession.repository;

import com.jvc.studyroom.domain.studySession.entity.SessionStatus;
import com.jvc.studyroom.domain.studySession.entity.StudySession;
import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.UUID;
import org.springframework.data.domain.Sort;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.query.Param;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface StudySessionRepository extends R2dbcRepository<StudySession, UUID> {

  @Query("SELECT * FROM study_sessions WHERE start_time BETWEEN :startTime AND :endTime")
  Flux<StudySession> findByStartTimeBetween(
      @Param("startTime") OffsetDateTime startTime,
      @Param("endTime") OffsetDateTime endTime
  );

  Flux<StudySession> findAll(Sort sort);

  Mono<StudySession> findBySessionId(UUID sessionId);

  Flux<StudySession> findByStudentId(UUID studentId);

  Flux<StudySession> findAllByStudentIdAndSessionStatusIn(UUID studentId,
      Collection<SessionStatus> status);


}