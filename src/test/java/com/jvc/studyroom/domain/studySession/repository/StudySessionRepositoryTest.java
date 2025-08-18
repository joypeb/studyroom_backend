package com.jvc.studyroom.domain.studySession.repository;

import com.jvc.studyroom.domain.studySession.entity.SessionStatus;
import com.jvc.studyroom.domain.studySession.entity.StudySession;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * StudySessionRepository Mock 테스트
 * 실제 DB 연결 없이 Repository 인터페이스의 동작을 검증
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("StudySessionRepository Mock 테스트")
class StudySessionRepositoryTest {

    @Mock
    private StudySessionRepository studySessionRepository;

    @Test
    @DisplayName("findAll - 정렬 조건으로 모든 세션 조회")
    void findAll_WithSort_Success() {
        // given
        Sort sort = Sort.by(Sort.Order.desc("createdAt"));
        UUID sessionId1 = UUID.randomUUID();
        UUID sessionId2 = UUID.randomUUID();
        UUID studentId = UUID.randomUUID();

        StudySession session1 = createTestSession(sessionId1, studentId, SessionStatus.ACTIVE);
        StudySession session2 = createTestSession(sessionId2, studentId, SessionStatus.COMPLETED);

        when(studySessionRepository.findAll(sort))
                .thenReturn(Flux.just(session1, session2));

        // when
        Flux<StudySession> result = studySessionRepository.findAll(sort);

        // then
        StepVerifier.create(result)
                .expectNext(session1)
                .expectNext(session2)
                .verifyComplete();

        verify(studySessionRepository).findAll(sort);
    }

    @Test
    @DisplayName("findBySessionId - 세션 ID로 특정 세션 조회")
    void findBySessionId_Success() {
        // given
        UUID sessionId = UUID.randomUUID();
        UUID studentId = UUID.randomUUID();
        StudySession session = createTestSession(sessionId, studentId, SessionStatus.ACTIVE);

        when(studySessionRepository.findBySessionId(sessionId))
                .thenReturn(Mono.just(session));

        // when
        Mono<StudySession> result = studySessionRepository.findBySessionId(sessionId);

        // then
        StepVerifier.create(result)
                .expectNext(session)
                .verifyComplete();

        verify(studySessionRepository).findBySessionId(sessionId);
    }

    @Test
    @DisplayName("findBySessionId - 존재하지 않는 세션 조회")
    void findBySessionId_NotFound() {
        // given
        UUID sessionId = UUID.randomUUID();

        when(studySessionRepository.findBySessionId(sessionId))
                .thenReturn(Mono.empty());

        // when
        Mono<StudySession> result = studySessionRepository.findBySessionId(sessionId);

        // then
        StepVerifier.create(result)
                .verifyComplete();

        verify(studySessionRepository).findBySessionId(sessionId);
    }

    @Test
    @DisplayName("save - 새로운 세션 저장")
    void save_NewSession_Success() {
        // given
        UUID sessionId = UUID.randomUUID();
        UUID studentId = UUID.randomUUID();
        StudySession newSession = createTestSession(null, studentId, SessionStatus.READY);
        StudySession savedSession = createTestSession(sessionId, studentId, SessionStatus.READY);

        when(studySessionRepository.save(newSession))
                .thenReturn(Mono.just(savedSession));

        // when
        Mono<StudySession> result = studySessionRepository.save(newSession);

        // then
        StepVerifier.create(result)
                .expectNext(savedSession)
                .verifyComplete();

        verify(studySessionRepository).save(newSession);
    }

    @Test
    @DisplayName("findByStudentId - 학생 ID로 세션 목록 조회")
    void findByStudentId_Success() {
        // given
        UUID studentId = UUID.randomUUID();
        StudySession session1 = createTestSession(UUID.randomUUID(), studentId, SessionStatus.COMPLETED);
        StudySession session2 = createTestSession(UUID.randomUUID(), studentId, SessionStatus.ACTIVE);

        when(studySessionRepository.findByStudentId(studentId))
                .thenReturn(Flux.just(session1, session2));

        // when
        Flux<StudySession> result = studySessionRepository.findByStudentId(studentId);

        // then
        StepVerifier.create(result)
                .expectNext(session1)
                .expectNext(session2)
                .verifyComplete();

        verify(studySessionRepository).findByStudentId(studentId);
    }

    @Test
    @DisplayName("findAllByStudentIdAndSessionStatusIn - 학생 ID와 세션 상태로 조회")
    void findAllByStudentIdAndSessionStatusIn_Success() {
        // given
        UUID studentId = UUID.randomUUID();
        List<SessionStatus> statuses = List.of(SessionStatus.ACTIVE, SessionStatus.PAUSED);
        StudySession activeSession = createTestSession(UUID.randomUUID(), studentId, SessionStatus.ACTIVE);
        StudySession pausedSession = createTestSession(UUID.randomUUID(), studentId, SessionStatus.PAUSED);

        when(studySessionRepository.findAllByStudentIdAndSessionStatusIn(studentId, statuses))
                .thenReturn(Flux.just(activeSession, pausedSession));

        // when
        Flux<StudySession> result = studySessionRepository.findAllByStudentIdAndSessionStatusIn(studentId, statuses);

        // then
        StepVerifier.create(result)
                .expectNext(activeSession)
                .expectNext(pausedSession)
                .verifyComplete();

        verify(studySessionRepository).findAllByStudentIdAndSessionStatusIn(studentId, statuses);
    }

    @Test
    @DisplayName("findById - ID로 세션 조회")
    void findById_Success() {
        // given
        UUID sessionId = UUID.randomUUID();
        UUID studentId = UUID.randomUUID();
        StudySession session = createTestSession(sessionId, studentId, SessionStatus.ACTIVE);

        when(studySessionRepository.findById(sessionId))
                .thenReturn(Mono.just(session));

        // when
        Mono<StudySession> result = studySessionRepository.findById(sessionId);

        // then
        StepVerifier.create(result)
                .expectNext(session)
                .verifyComplete();

        verify(studySessionRepository).findById(sessionId);
    }

    @Test
    @DisplayName("existsById - 세션 존재 여부 확인")
    void existsById_True() {
        // given
        UUID sessionId = UUID.randomUUID();

        when(studySessionRepository.existsById(sessionId))
                .thenReturn(Mono.just(true));

        // when
        Mono<Boolean> result = studySessionRepository.existsById(sessionId);

        // then
        StepVerifier.create(result)
                .expectNext(true)
                .verifyComplete();

        verify(studySessionRepository).existsById(sessionId);
    }

    @Test
    @DisplayName("deleteById - 세션 삭제")
    void deleteById_Success() {
        // given
        UUID sessionId = UUID.randomUUID();

        when(studySessionRepository.deleteById(sessionId))
                .thenReturn(Mono.empty());

        // when
        Mono<Void> result = studySessionRepository.deleteById(sessionId);

        // then
        StepVerifier.create(result)
                .verifyComplete();

        verify(studySessionRepository).deleteById(sessionId);
    }

    private StudySession createTestSession(UUID sessionId, UUID studentId, SessionStatus status) {
        return StudySession.builder()
                .sessionId(sessionId)
                .studentId(studentId)
                .seatId(UUID.randomUUID())
                .startTime(OffsetDateTime.now())
                .plannedEndTime(OffsetDateTime.now().plusHours(2))
                .sessionStatus(status)
                .totalStudyMinutes(0)
                .totalBreakMinutes(0)
                .pauseCount(0)
                .version(1)
                .createdAt(OffsetDateTime.now())
                .updatedAt(OffsetDateTime.now())
                .createdBy(studentId)
                .build();
    }
}
