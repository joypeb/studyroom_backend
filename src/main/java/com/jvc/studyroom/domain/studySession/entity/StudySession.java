package com.jvc.studyroom.domain.studySession.entity;

import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true) // 기존 객체 복사 기능 : setter 없이 update 할 때 유용하게 사용 가능
@Table("study_sessions")
public class StudySession {

  @Id
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
  UUID endedBy;
  int version;
  OffsetDateTime createdAt;
  OffsetDateTime updatedAt;
  UUID createdBy;

  @Override
  public String toString() {
    return "StudySession{" +
        "sessionId=" + sessionId +
        ", studentId=" + studentId +
        ", seatId=" + seatId +
        ", startTime=" + startTime +
        ", endTime=" + endTime +
        ", plannedEndTime=" + plannedEndTime +
        ", sessionStatus=" + sessionStatus +
        ", totalStudyMinutes=" + totalStudyMinutes +
        ", totalBreakMinutes=" + totalBreakMinutes +
        ", pauseCount=" + pauseCount +
        ", endReason=" + endReason +
        ", endedBy=" + endedBy +
        ", version=" + version +
        ", createdAt=" + createdAt +
        ", updatedAt=" + updatedAt +
        ", createdBy=" + createdBy +
        '}';
  }
}
