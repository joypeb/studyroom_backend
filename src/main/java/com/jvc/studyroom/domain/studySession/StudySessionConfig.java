package com.jvc.studyroom.domain.studySession;

import com.jvc.studyroom.domain.studySession.repository.StudySessionRepository;
import com.jvc.studyroom.domain.studySession.service.StudySessionServiceV1;
import com.jvc.studyroom.domain.studySession.service.StudySessionService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StudySessionConfig {
    @Bean
    public StudySessionService studySessionService(StudySessionRepository repository) {
        return new StudySessionServiceV1(repository);
    }
}
