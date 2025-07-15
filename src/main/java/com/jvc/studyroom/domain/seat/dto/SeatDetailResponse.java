package com.jvc.studyroom.domain.seat.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.jvc.studyroom.common.enums.SeatStatus;
import java.time.OffsetDateTime;
import java.util.UUID;

public record SeatDetailResponse(
    UUID seatId,
    String seatNumber,
    String roomName,
    Integer positionX,
    Integer positionY,
    Integer rotation,
    SeatStatus seatStatus,
    Boolean hasPowerOutlet,
    Boolean hasDeskLamp,
    Boolean hasLocker,
    Boolean isNearWindow,
    UUID assignedStudentId,
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    OffsetDateTime assignedAt,
    UUID assignedBy,
    Boolean isActive,
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    OffsetDateTime createdAt,
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    OffsetDateTime updatedAt
) {}