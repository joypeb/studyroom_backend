package com.jvc.studyroom.domain.studySession.converter;

import com.jvc.studyroom.domain.studySession.dto.SessionCreateData;
import com.jvc.studyroom.domain.studySession.dto.SessionCreateRequest;
import com.jvc.studyroom.domain.studySession.entity.StudySession;
public class SessionCreateMapper {
    public static StudySession toEntity(SessionCreateRequest dto, SessionCreateData data) {
        return StudySession.builder()
                .studentId(data.studentId())
                .seatId(data.seatId())
                .plannedEndTime(dto.plannedEndTime())
                .sessionStatus(data.sessionStatus())
                .version(data.version())
                .startTime(data.startTime())
                .createdBy(data.createdBy())
                .build();
    }
}
