package com.jvc.studyroom.domain.studySession.converter;

import com.jvc.studyroom.domain.studySession.dto.SessionHistoryResponse;
import com.jvc.studyroom.domain.studySession.entity.StudySession;

public class SessionFindMapper {
    public static SessionHistoryResponse toSessionHistoryResponse(StudySession studySession) {
        return new SessionHistoryResponse(
                studySession.getSessionId(),
                studySession.getStudentId(),
                studySession.getSeatId(),
                studySession.getStartTime(),
                studySession.getEndTime(),
                studySession.getPlannedEndTime(),
                studySession.getSessionStatus(),
                studySession.getTotalStudyMinutes(),
                studySession.getTotalBreakMinutes(),
                studySession.getPauseCount(),
                studySession.getEndReason()
        );
    }
}
