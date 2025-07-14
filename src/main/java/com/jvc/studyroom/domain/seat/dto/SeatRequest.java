package com.jvc.studyroom.domain.seat.dto;

import com.jvc.studyroom.common.enums.SeatStatus;

public record SeatRequest(
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
    Boolean isActive
) {}
