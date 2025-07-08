package com.jvc.studyroom.domain.studySession.entity;
import com.jvc.studyroom.domain.studySession.entity.EndReasonType;
import com.jvc.studyroom.domain.studySession.entity.SessionStatus;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.relational.core.mapping.Table;

import java.time.OffsetDateTime;

@Table("study_sessions")
@Getter
@Setter
public class StudySession {
    String sessionId;
    String studentId;
    String seatId;
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
    OffsetDateTime updated_At;
    String createdBy;
}
