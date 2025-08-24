package com.jvc.studyroom.domain.studySession.converter;

import com.jvc.studyroom.common.utils.TimeUtil;
import com.jvc.studyroom.domain.studySession.dto.SessionCreateData;
import com.jvc.studyroom.domain.studySession.dto.SessionCreateRequest;
import com.jvc.studyroom.domain.studySession.entity.StudySession;
import java.time.OffsetDateTime;

public class SessionCreateMapper {

  /**
   * SessionCreateRequest의 duration을 기반으로 plannedEndTime을 자동 계산
   */
  public static StudySession toEntity(SessionCreateRequest dto, SessionCreateData data) {
    // 시작 시간 + duration으로 plannedEndTime 계산
    OffsetDateTime plannedEndTime = data.startTime().plusHours(dto.duration().getHours());
    OffsetDateTime now = TimeUtil.nowInKorea();

    return StudySession.builder()
        .studentId(data.studentId())
        .seatId(data.seatId())
        .startTime(data.startTime())
        .plannedEndTime(plannedEndTime)
        .sessionStatus(data.sessionStatus())
        .totalStudyMinutes(0)  // 시작 시에는 0
        .totalBreakMinutes(0)  // 시작 시에는 0
        .pauseCount(0)         // 시작 시에는 0
        .version(data.version())
        .createdAt(now)
        .updatedAt(now)        // 시간 추적을 위해 초기 설정
        .createdBy(data.createdBy())
        .build();
  }
}
