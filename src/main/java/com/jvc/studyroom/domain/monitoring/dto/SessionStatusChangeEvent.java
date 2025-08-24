package com.jvc.studyroom.domain.monitoring.dto;

import com.jvc.studyroom.common.enums.SeatStatus;
import com.jvc.studyroom.domain.studySession.entity.SessionStatus;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
* 세션 상태 변경 이벤트
*/
public record SessionStatusChangeEvent(
        UUID sessionId,
        UUID seatId,
        String seatNumber,
        String roomName,
        SessionStatus previousStatus,
        SessionStatus currentStatus,
        UUID studentId,
        String eventType,
        OffsetDateTime timestamp,
        String reason
) {
    public static SessionStatusChangeEvent sessionStarted(UUID sessionId, UUID seatId, String seatNumber, String roomName, UUID studentId) {
        return new SessionStatusChangeEvent(
                sessionId, seatId, seatNumber, roomName,
                null, SessionStatus.ACTIVE,
                studentId, "SESSION_STARTED",
                OffsetDateTime.now(), "세션 시작"
        );
    }

    public static SessionStatusChangeEvent sessionPaused(UUID sessionId, UUID seatId, String seatNumber, String roomName, UUID studentId) {
        return new SessionStatusChangeEvent(
                sessionId, seatId, seatNumber, roomName,
                SessionStatus.ACTIVE, SessionStatus.PAUSED,
                studentId, "SESSION_PAUSED",
                OffsetDateTime.now(), "세션 일시정지"
        );
    }

    public static SessionStatusChangeEvent sessionResumed(UUID sessionId, UUID seatId, String seatNumber, String roomName, UUID studentId) {
        return new SessionStatusChangeEvent(
                sessionId, seatId, seatNumber, roomName,
                SessionStatus.PAUSED, SessionStatus.ACTIVE,
                studentId, "SESSION_RESUMED",
                OffsetDateTime.now(), "세션 재개"
        );
    }

    public static SessionStatusChangeEvent sessionCompleted(UUID sessionId, UUID seatId, String seatNumber, String roomName, UUID studentId) {
        return new SessionStatusChangeEvent(
                sessionId, seatId, seatNumber, roomName,
                SessionStatus.ACTIVE, SessionStatus.COMPLETED,
                studentId, "SESSION_COMPLETED",
                OffsetDateTime.now(), "세션 완료"
        );
    }

    public static SessionStatusChangeEvent sessionCancelled(UUID sessionId, UUID seatId, String seatNumber, String roomName, UUID studentId) {
        return new SessionStatusChangeEvent(
                sessionId, seatId, seatNumber, roomName,
                SessionStatus.ACTIVE, SessionStatus.CANCELLED,
                studentId, "SESSION_CANCELLED",
                OffsetDateTime.now(), "세션 취소"
        );
    }
}
