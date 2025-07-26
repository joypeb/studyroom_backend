package com.jvc.studyroom.domain.monitoring.dto;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

/**
 * 스터디 세션 모니터링 응답 데이터
 * 학생-좌석 1:1 매핑 기반 모니터링
 */
public record StudySessionMonitoringResponse(
        OffsetDateTime timestamp,
        List<SessionMonitoringData> sessions,
        Map<String, List<SessionMonitoringData>> groupSessions, // 그룹별 세션들
        int totalActiveSessions,
        int totalStudents
) {
        public static StudySessionMonitoringResponse empty() {
                return new StudySessionMonitoringResponse(
                        OffsetDateTime.now(),
                        List.of(),
                        Map.of(),
                        0,
                        0
                );
        }
}