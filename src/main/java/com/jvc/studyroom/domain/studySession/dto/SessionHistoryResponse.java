package com.jvc.studyroom.domain.studySession.dto;

import com.jvc.studyroom.domain.studySession.entity.EndReasonType;
import com.jvc.studyroom.domain.studySession.entity.SessionStatus;

import java.time.OffsetDateTime;
import java.util.UUID;

public record SessionHistoryResponse(
        UUID sessionId,
        UUID studentId,
        UUID seatId,
        OffsetDateTime startTime,
        OffsetDateTime endTime,
        OffsetDateTime plannedEndTime,
        SessionStatus sessionStatus,
        int totalStudyMinutes,
        int totalBreakMinutes,
        int pauseCount,
        EndReasonType endReason
) {
}
