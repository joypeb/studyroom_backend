package com.jvc.studyroom.domain.seat.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.jvc.studyroom.common.enums.SeatStatus;
import java.time.OffsetDateTime;
import java.util.UUID;

public record SeatResponse(
        UUID seatId,
        String seatNumber,
        String roomName,
        SeatStatus seatStatus,
        UUID assignedStudentId,
        Boolean isActive,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
        OffsetDateTime createdAt) {
}
