package com.jvc.studyroom.domain.studySession.service;

import com.jvc.studyroom.domain.studySession.entity.SessionStatus;
import com.jvc.studyroom.domain.studySession.entity.StudySession;
import com.jvc.studyroom.common.utils.TimeUtil;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 스터디 세션의 시간 계산을 전담하는 도메인 서비스
 */
@Slf4j
@Component
public class StudySessionTimeCalculator {

    /**
     * 현재 시점에서의 실시간 공부 시간 계산 (분)
     * - ACTIVE: 기존 공부시간 + 현재 활성 구간 시간
     * - PAUSED: 기존 공부시간만
     * - COMPLETED/INTERRUPTED: 최종 기록된 공부시간
     */
    public long calculateCurrentStudyMinutes(StudySession session) {
        if (session.getStartTime() == null) {
            return 0;
        }

        OffsetDateTime now = TimeUtil.nowInKorea();

        switch (session.getSessionStatus()) {
            case ACTIVE:
                long currentActiveTime = calculateActiveTimeSinceLastUpdate(session, now);
                return session.getTotalStudyMinutes() + currentActiveTime;

            case PAUSED:
            case COMPLETED:
            case INTERRUPTED:
                return session.getTotalStudyMinutes();

            default:
                log.warn("예상하지 못한 세션 상태: {}", session.getSessionStatus());
                return session.getTotalStudyMinutes();
        }
    }

    /**
     * 마지막 업데이트 이후 ACTIVE 상태로 경과된 시간 계산 (분)
     */
    public long calculateActiveTimeSinceLastUpdate(StudySession session, OffsetDateTime currentTime) {
        if (session.getSessionStatus() != SessionStatus.ACTIVE) {
            return 0;
        }

        OffsetDateTime lastUpdateTime = getLastUpdateTime(session);
        return ChronoUnit.MINUTES.between(lastUpdateTime, currentTime);
    }

    /**
     * 마지막 업데이트 이후 PAUSED 상태로 경과된 시간 계산 (분)
     */
    public long calculateBreakTimeSinceLastUpdate(StudySession session, OffsetDateTime currentTime) {
        if (session.getSessionStatus() != SessionStatus.PAUSED) {
            return 0;
        }

        OffsetDateTime lastUpdateTime = getLastUpdateTime(session);
        return ChronoUnit.MINUTES.between(lastUpdateTime, currentTime);
    }

    /**
     * 세션 상태 변경 시 시간 업데이트를 위한 계산
     */
    public StudySessionTimeUpdate calculateTimeUpdateForStatusChange(
            StudySession session, 
            SessionStatus newStatus, 
            OffsetDateTime changeTime) {
        
        SessionStatus oldStatus = session.getSessionStatus();
        
        long additionalStudyMinutes = 0;
        long additionalBreakMinutes = 0;
        
        // 이전 상태가 ACTIVE였다면 공부 시간 누적
        if (oldStatus == SessionStatus.ACTIVE) {
            additionalStudyMinutes = calculateActiveTimeSinceLastUpdate(session, changeTime);
        }
        
        // 이전 상태가 PAUSED였다면 휴식 시간 누적
        if (oldStatus == SessionStatus.PAUSED) {
            additionalBreakMinutes = calculateBreakTimeSinceLastUpdate(session, changeTime);
        }
        
        // PAUSED로 변경 시 pause 카운트 증가 여부
        boolean shouldIncrementPauseCount = (newStatus == SessionStatus.PAUSED && oldStatus != SessionStatus.PAUSED);
        
        return new StudySessionTimeUpdate(
            additionalStudyMinutes,
            additionalBreakMinutes,
            shouldIncrementPauseCount
        );
    }

    /**
     * 마지막 업데이트 시간 추출
     */
    private OffsetDateTime getLastUpdateTime(StudySession session) {
        if (session.getUpdatedAt() != null) {
            return session.getUpdatedAt();
        }
        if (session.getCreatedAt() != null) {
            return session.getCreatedAt();
        }
        return session.getStartTime();
    }

    /**
     * 시간 업데이트 정보를 담는 DTO
     */
    public record StudySessionTimeUpdate(
        long additionalStudyMinutes,
        long additionalBreakMinutes,
        boolean shouldIncrementPauseCount
    ) {}
}
