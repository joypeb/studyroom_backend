package com.jvc.studyroom.domain.monitoring.dto;

import com.jvc.studyroom.common.utils.DateTimeUtil;
import com.jvc.studyroom.domain.seat.model.Seat;
import com.jvc.studyroom.domain.studySession.entity.SessionStatus;
import com.jvc.studyroom.domain.studySession.entity.StudySession;
import com.jvc.studyroom.domain.user.model.User;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * 개별 세션 모니터링 데이터
 * 학생, 좌석, 세션 정보를 통합
 */
public record SessionMonitoringData(
        // 세션 정보
        UUID sessionId,
        SessionStatus sessionStatus,
        SessionDuration sessionDuration,    // 1H, 2H, 4H
        OffsetDateTime sessionStartTime,
        OffsetDateTime sessionEndTime,
        OffsetDateTime plannedEndTime,
        boolean isExtensionAvailable,       // 연장 가능 여부
        boolean isExtensionRequested,       // 연장 요청됨

        // 학생 정보
        UUID studentId,
        String studentName,
        String studentPhone,

        // 좌석 정보
        UUID seatId,
        String seatNumber,
        String groupName,                   // 좌석이 속한 그룹명

        // 시간 정보
        StudyTimeInfo timeInfo
) {

    /**
     * 세션 지속시간 Enum
     */
    public enum SessionDuration {
        ONE_HOUR(1, "1시간"),
        TWO_HOURS(2, "2시간"),
        FOUR_HOURS(4, "4시간");

        private final int hours;
        private final String displayName;

        SessionDuration(int hours, String displayName) {
            this.hours = hours;
            this.displayName = displayName;
        }

        public int getHours() {
            return hours;
        }

        public String getDisplayName() {
            return displayName;
        }

        public long getMinutes() {
            return hours * 60L;
        }
    }

    /**
     * 학습 시간 정보
     */
    public record StudyTimeInfo(
            long elapsedMinutes,           // 경과 시간 (분)
            long remainingMinutes,         // 남은 시간 (분)
            double progressPercentage,     // 진행률 (%)
            boolean isNearExpiration,      // 만료 임박 (10분 이내)
            boolean isExpired,             // 시간 만료
            boolean isOvertime             // 연장 시간 초과
    ) {
        public String formatElapsedTime() {
            return DateTimeUtil.formatMinutes(elapsedMinutes);
        }

        public String formatRemainingTime() {
            if (remainingMinutes <= 0) {
                return isOvertime ? "초과됨" : "만료";
            }
            return DateTimeUtil.formatMinutes(remainingMinutes);
        }

        /**
         * 세션 정보로부터 StudyTimeInfo 생성
         */
        public static StudyTimeInfo from(
                OffsetDateTime startTime,
                OffsetDateTime plannedEndTime,
                SessionStatus status) {

            if (startTime == null) {
                return new StudyTimeInfo(0, 0, 0.0, false, false, false);
            }

            OffsetDateTime now = OffsetDateTime.now();
            long totalMinutes = java.time.temporal.ChronoUnit.MINUTES.between(startTime, plannedEndTime);
            long elapsedMinutes = java.time.temporal.ChronoUnit.MINUTES.between(startTime, now);
            long remainingMinutes = totalMinutes - elapsedMinutes;

            double progressPercentage = totalMinutes > 0 ?
                    Math.min(100.0, (double) elapsedMinutes / totalMinutes * 100.0) : 0.0;

            boolean isNearExpiration = remainingMinutes <= 10 && remainingMinutes > 0;
            boolean isExpired = remainingMinutes <= 0;
            boolean isOvertime = isExpired && elapsedMinutes > totalMinutes + 30; // 30분 초과 시 오버타임

            return new StudyTimeInfo(
                    Math.max(0, elapsedMinutes),
                    Math.max(0, remainingMinutes),
                    Math.round(progressPercentage * 100.0) / 100.0,
                    isNearExpiration,
                    isExpired,
                    isOvertime
            );
        }
    }

    /**
     * 엔티티들로부터 SessionMonitoringData 생성
     */
    public static SessionMonitoringData from(
            StudySession session,
            User student,
            Seat seat) {

        // 세션 지속시간 계산 (plannedEndTime - startTime)
        SessionDuration duration = calculateSessionDuration(session.getStartTime(), session.getPlannedEndTime());

        return new SessionMonitoringData(
                session.getSessionId(),
                session.getSessionStatus(),
                duration,
                session.getStartTime(),
                session.getEndTime(),
                session.getPlannedEndTime(),
                isExtensionAvailable(session),
                false, // TODO: 연장 요청 상태는 별도 관리 필요
                student.getUserId(),
                student.getName(),
                student.getPhoneNumber(),
                seat.getSeatId(),
                seat.getSeatNumber(),
                seat.getRoomName(), // roomName을 groupName으로 사용
                StudyTimeInfo.from(session.getStartTime(), session.getPlannedEndTime(), session.getSessionStatus())
        );
    }

    /**
     * 세션 지속시간 계산
     */
    private static SessionDuration calculateSessionDuration(OffsetDateTime startTime, OffsetDateTime plannedEndTime) {
        if (startTime == null || plannedEndTime == null) {
            return SessionDuration.ONE_HOUR; // 기본값
        }

        long minutes = java.time.temporal.ChronoUnit.MINUTES.between(startTime, plannedEndTime);
        long hours = minutes / 60;

        if (hours <= 1) return SessionDuration.ONE_HOUR;
        if (hours <= 2) return SessionDuration.TWO_HOURS;
        return SessionDuration.FOUR_HOURS;
    }

    /**
     * 연장 가능 여부 확인
     */
    private static boolean isExtensionAvailable(com.jvc.studyroom.domain.studySession.entity.StudySession session) {
        if (session.getSessionStatus() != SessionStatus.ACTIVE) {
            return false;
        }

        OffsetDateTime now = OffsetDateTime.now();
        OffsetDateTime plannedEndTime = session.getPlannedEndTime();

        if (plannedEndTime == null) {
            return false;
        }

        // 종료 10분 전부터 연장 가능
        OffsetDateTime extensionAvailableTime = plannedEndTime.minusMinutes(10);
        return now.isAfter(extensionAvailableTime);
    }

    /**
     * 현재 세션이 활성 상태인지 확인
     */
    public boolean isActiveSession() {
        return sessionStatus == SessionStatus.ACTIVE || sessionStatus == SessionStatus.PAUSED;
    }

    /**
     * 만료 임박 세션인지 확인
     */
    public boolean isNearExpiration() {
        return timeInfo != null && timeInfo.isNearExpiration();
    }

    /**
     * 만료된 세션인지 확인
     */
    public boolean isExpired() {
        return timeInfo != null && timeInfo.isExpired();
    }

    /**
     * 오버타임 세션인지 확인
     */
    public boolean isOvertime() {
        return timeInfo != null && timeInfo.isOvertime();
    }

}