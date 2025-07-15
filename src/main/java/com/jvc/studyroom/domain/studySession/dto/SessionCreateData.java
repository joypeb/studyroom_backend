package com.jvc.studyroom.domain.studySession.dto;

import com.jvc.studyroom.domain.studySession.entity.SessionStatus;

import java.time.OffsetDateTime;
import java.util.UUID;

public record SessionCreateData (
        UUID studentId, UUID seatId, SessionStatus sessionStatus, int version, UUID createdBy

)
{
    public SessionCreateData(UUID studentId, UUID seatId, SessionStatus sessionStatus, int version, UUID createdBy) {
        this.studentId = studentId;
        this.seatId = seatId;
        this.sessionStatus = sessionStatus;
        this.version = version;
        this.createdBy = createdBy;
    }
}
