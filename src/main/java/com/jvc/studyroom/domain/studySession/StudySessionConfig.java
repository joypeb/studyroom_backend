package com.jvc.studyroom.domain.studySession;

import com.jvc.studyroom.domain.seat.service.SeatFindService;
import com.jvc.studyroom.domain.studySession.repository.StudySessionRepository;
import com.jvc.studyroom.domain.studySession.service.StudySessionServiceV1;
import com.jvc.studyroom.domain.studySession.service.StudySessionService;
import com.jvc.studyroom.domain.studySession.service.StudySessionTimeCalculator;
import com.jvc.studyroom.domain.user.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StudySessionConfig {
    @Bean
    public StudySessionService studySessionService(
            StudySessionRepository studySessionRepository,
            UserRepository userRepository,
            SeatFindService seatfindService,
            StudySessionTimeCalculator timeCalculator
    ) {
        return new StudySessionServiceV1(studySessionRepository, userRepository, seatfindService, timeCalculator);
    }
}
