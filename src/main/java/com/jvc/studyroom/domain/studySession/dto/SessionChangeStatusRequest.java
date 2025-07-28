package com.jvc.studyroom.domain.studySession.dto;

import com.jvc.studyroom.domain.studySession.entity.SessionStatus;

import java.util.UUID;

public record SessionChangeStatusRequest (
        UUID studySessionId,
        SessionStatus sessionStatus
)
{
}
