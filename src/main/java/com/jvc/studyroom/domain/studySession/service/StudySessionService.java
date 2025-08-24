package com.jvc.studyroom.domain.studySession.service;

import com.jvc.studyroom.domain.studySession.dto.*;
import com.jvc.studyroom.domain.user.model.User;
import org.springframework.data.domain.Sort;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;


public interface StudySessionService {

    Flux<StudySessionListResponse> getSessionList(Sort sort);

    Mono<StudySessionResponse> getSession(UUID sessionId);

    Mono<StudySessionCreateResponse> createSession(SessionCreateRequest request, User loginUser);

    Mono<Void> changeSessionStatus(SessionChangeStatusRequest request, User loginUser);

    Flux<SessionHistoryResponse> getSessionHistory(UUID studentId);

    Flux<SessionHistoryResponse> getCurrentSession(UUID studentId);
}
