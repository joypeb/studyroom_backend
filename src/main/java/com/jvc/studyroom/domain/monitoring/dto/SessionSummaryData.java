package com.jvc.studyroom.domain.monitoring.dto;

import com.jvc.studyroom.domain.studySession.entity.SessionStatus;
import com.jvc.studyroom.domain.studySession.entity.StudySession;
import com.jvc.studyroom.domain.user.model.User;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

/**
 * 세션 목록 데이터 (요약)
 */
public record SessionSummaryData(
    UUID sessionId,
    String studentName,
    OffsetDateTime sessionStartTime,
    OffsetDateTime plannedEndTime,
    SessionStatus sessionStatus,
    long studyMinutes,              // 실시간 공부 시간 (분)
    String formattedStudyTime       // "1시간 25분" 형태
) {

  /**
   * StudySession과 User로부터 SessionSummaryData 생성
   */
  public static SessionSummaryData from(StudySession session, User student) {
    long studyMinutes = calculateStudyMinutes(session);
    String formattedTime = formatStudyTime(studyMinutes);

    return new SessionSummaryData(
        session.getSessionId(),
        student != null ? student.getName() : "Unknown",
        session.getStartTime(),
        session.getPlannedEndTime(),
        session.getSessionStatus(),
        studyMinutes,
        formattedTime
    );
  }

  /**
   * 실시간 공부 시간 계산 (분) - 일시정지 시간 제외
   */
  private static long calculateStudyMinutes(StudySession session) {
    if (session.getStartTime() == null) {
      return 0;
    }

    OffsetDateTime now = OffsetDateTime.now();

    // 세션 상태에 따른 계산
    switch (session.getSessionStatus()) {
      case ACTIVE:
        // ACTIVE 상태: 기존 공부시간 + 마지막 재개 후 경과시간
        return session.getTotalStudyMinutes() + calculateCurrentActiveTime(session, now);

      case PAUSED:
        // PAUSED 상태: 기존 공부시간만 (현재 일시정지 중)
        return session.getTotalStudyMinutes();

      case COMPLETED:
        // 완료/종료 상태: DB에 저장된 총 공부시간
        return session.getTotalStudyMinutes();

      default:
        return session.getTotalStudyMinutes();
    }
  }

  /**
   * 현재 ACTIVE 상태에서의 경과 시간 계산 (마지막 상태 변경 시점부터 현재까지)
   */
  private static long calculateCurrentActiveTime(StudySession session, OffsetDateTime now) {
    // 실제로는 마지막 상태 변경 시간을 알아야 하지만,
    // 현재 구조에서는 대략적으로 계산
    // TODO: session_events 테이블이 있다면 더 정확한 계산 가능

    OffsetDateTime sessionStart = session.getStartTime();
    long totalElapsed = ChronoUnit.MINUTES.between(sessionStart, now);
    long recordedStudyTime = session.getTotalStudyMinutes();
    long recordedBreakTime = session.getTotalBreakMinutes();

    // 전체 경과시간 - 기록된 공부시간 - 기록된 휴식시간 = 현재 활성 구간
    long currentActiveTime = totalElapsed - recordedStudyTime - recordedBreakTime;

    return Math.max(0, currentActiveTime);
  }

  /**
   * 공부 시간을 "1시간 25분" 형태로 포맷
   */
  private static String formatStudyTime(long totalMinutes) {
    if (totalMinutes < 0) {
      return "0분";
    }

    long hours = totalMinutes / 60;
    long minutes = totalMinutes % 60;

    if (hours == 0) {
      return minutes + "분";
    } else if (minutes == 0) {
      return hours + "시간";
    } else {
      return hours + "시간 " + minutes + "분";
    }
  }
}
