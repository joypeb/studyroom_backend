package com.jvc.studyroom.domain.studySession.Entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.relational.core.mapping.Table;

import java.time.OffsetDateTime;

@Table("study_sessions")
@Getter
@Setter
public class StudySession {
    String session_id;
    String student_id;
    String seat_id;
    OffsetDateTime start_time;
    OffsetDateTime end_time;
    OffsetDateTime planned_end_time;
    SessionStatus session_status;
    int total_study_minutes;
    int total_break_minutes;
    int pause_count;
    EndReasonType end_reason;
    String ended_by;
    int version;
    OffsetDateTime created_at;
    OffsetDateTime updated_at;
    String created_by;
}
