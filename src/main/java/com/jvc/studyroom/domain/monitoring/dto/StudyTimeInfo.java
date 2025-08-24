package com.jvc.studyroom.domain.monitoring.dto;

import com.jvc.studyroom.domain.studySession.entity.StudySession;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

/*
* 학습 시간 정보
 */
public record StudyTimeInfo(
        long currentSessionMinutes,     // 현재 세션 경과 시간
        long todayTotalMinutes,         // 오늘 총 공부 시간
        long consecutiveStudyMinutes,   // 활성화 중인  todo. 확인해야함
        OffsetDateTime lastActivityTime // 마지막 활동 시간
) {
    /**
     * 세션 정보들로부터 StudyTimeInfo 생성
     */
    public static StudyTimeInfo from(StudySession activeSession, List<StudySession> todaySessions) {
        long currentSessionMinutes = 0;
        long consecutiveStudyMinutes = 0;
        OffsetDateTime lastActivityTime = null;

        if (activeSession != null) {
            currentSessionMinutes = ChronoUnit.MINUTES
                    .between(activeSession.getStartTime(), OffsetDateTime.now());
            consecutiveStudyMinutes = activeSession.getTotalStudyMinutes();
            lastActivityTime = activeSession.getUpdatedAt();
        }

        long todayTotalMinutes = todaySessions.stream()
                .mapToLong(StudySession::getTotalStudyMinutes)
                .sum();

        return new StudyTimeInfo(
                currentSessionMinutes,
                todayTotalMinutes,
                consecutiveStudyMinutes,
                lastActivityTime
        );
    }
}
