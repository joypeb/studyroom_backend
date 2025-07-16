package com.jvc.studyroom.domain.studySession.entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.relational.core.mapping.Table;
import java.time.OffsetDateTime;
import java.util.UUID;
@Getter
@NoArgsConstructor
@Table("study_sessions")
public class StudySession {
    UUID sessionId;
    UUID studentId;
    UUID seatId;
    OffsetDateTime startTime;
    OffsetDateTime endTime;
    OffsetDateTime plannedEndTime;
    SessionStatus sessionStatus;
    int totalStudyMinutes;
    int totalBreakMinutes;
    int pauseCount;
    EndReasonType endReason;
    String endedBy;
    int version;
    OffsetDateTime createdAt;
    OffsetDateTime updatedAt;
    UUID createdBy;

    private StudySession(UUID studentId, UUID seatId, OffsetDateTime plannedEndTime, SessionStatus sessionStatus, int version, UUID createdBy) {
        this.studentId = studentId;
        this.seatId = seatId;
        this.plannedEndTime = plannedEndTime;
        this.sessionStatus = sessionStatus;
        this.version = version;
        this.createdBy = createdBy;
    }

    public static StudySession ofCreateEntity(UUID studentId, UUID seatId, OffsetDateTime plannedEndTime, SessionStatus sessionStatus, int version, UUID createdBy) {
        return new StudySession(studentId, seatId, plannedEndTime, sessionStatus,  version, createdBy);
    }
}
