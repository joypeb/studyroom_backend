package com.jvc.studyroom.domain.studySession.dto;

import com.jvc.studyroom.domain.studySession.entity.SessionStatus;

import java.time.OffsetDateTime;
import java.util.UUID;

public record SessionCreateData (
        UUID studentId,
        UUID seatId,
        SessionStatus sessionStatus,
        int version,
        OffsetDateTime startTime,
        UUID createdBy

)
{
}
