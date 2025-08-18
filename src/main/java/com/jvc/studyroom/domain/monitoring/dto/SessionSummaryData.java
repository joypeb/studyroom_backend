package com.jvc.studyroom.domain.monitoring.dto;

import com.jvc.studyroom.domain.studySession.entity.SessionStatus;
import com.jvc.studyroom.domain.studySession.entity.StudySession;
import com.jvc.studyroom.domain.studySession.service.StudySessionTimeCalculator;
import com.jvc.studyroom.domain.user.model.User;
import com.jvc.studyroom.common.utils.TimeFormatUtil;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * 세션 목록 데이터 (요약) - 계산 로직은 외부에 위임
 */
public record SessionSummaryData(
    UUID sessionId,
    String studentName,
    OffsetDateTime sessionStartTime,
    OffsetDateTime plannedEndTime,
    SessionStatus sessionStatus,
    long studyMinutes,
    String formattedStudyTime
) {

    /**
     * StudySession과 User로부터 SessionSummaryData 생성
     */
    public static SessionSummaryData from(
            StudySession session, 
            User student, 
            StudySessionTimeCalculator timeCalculator) {
        
        long studyMinutes = timeCalculator.calculateCurrentStudyMinutes(session);
        String formattedTime = TimeFormatUtil.formatStudyTime(studyMinutes);

        return new SessionSummaryData(
            session.getSessionId(),
            student != null ? student.getName() : "Unknown",
            session.getStartTime(),
            session.getPlannedEndTime(),
            session.getSessionStatus(),
            studyMinutes,
            formattedTime
        );
    }
}
