package com.jvc.studyroom.domain.studySession.service;

import com.jvc.studyroom.domain.studySession.dto.StudySessionListResponse;
import com.jvc.studyroom.domain.studySession.dto.StudySessionResponse;
import org.springframework.data.domain.Sort;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;


public interface StudySessionService {

    Flux<StudySessionListResponse> getSessionList(Sort sort);

    Mono<StudySessionResponse> getSession(UUID sessionId);
}
