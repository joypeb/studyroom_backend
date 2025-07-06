package com.jvc.studyroom.domain.studySession.service;

import com.jvc.studyroom.domain.studySession.dto.StudySessionList;
import reactor.core.publisher.Flux;

public interface StudySessionService {

    Flux<StudySessionList> getSessionList();
}
