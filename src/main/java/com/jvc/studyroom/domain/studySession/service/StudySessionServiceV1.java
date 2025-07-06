package com.jvc.studyroom.domain.studySession.service;

import com.jvc.studyroom.domain.studySession.dto.StudySessionList;
import com.jvc.studyroom.domain.studySession.repository.StudySessionRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;

@RequiredArgsConstructor
public class StudySessionServiceV1 implements StudySessionService{
    private final StudySessionRepository repository;

    @Override
    public Flux<StudySessionList> getSessionList() {
        return null;
    }
}
