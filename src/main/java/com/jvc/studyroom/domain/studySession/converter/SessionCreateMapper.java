package com.jvc.studyroom.domain.studySession.converter;

import com.jvc.studyroom.domain.studySession.dto.SessionCreateData;
import com.jvc.studyroom.domain.studySession.dto.SessionCreateRequest;
import com.jvc.studyroom.domain.studySession.entity.StudySession;

import static com.jvc.studyroom.domain.studySession.entity.StudySession.ofCreateEntity;

public class SessionCreateMapper {
    public static StudySession toEntity(SessionCreateRequest dto, SessionCreateData data) {
        return ofCreateEntity(
                data.studentId(),
                data.seatId(),
                dto.plannedEndTime(),
                data.sessionStatus(),
                data.version(),
                data.createdBy()
        );
    }
}
