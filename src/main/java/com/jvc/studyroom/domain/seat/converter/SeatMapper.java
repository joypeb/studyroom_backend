package com.jvc.studyroom.domain.seat.converter;

import com.jvc.studyroom.domain.seat.dto.SeatDetailResponse;
import com.jvc.studyroom.domain.seat.dto.SeatResponse;
import com.jvc.studyroom.domain.seat.model.Seat;

public class SeatMapper {

    public static SeatResponse toSeatResponse(Seat seat) {
        return new SeatResponse(
                seat.getSeatId(),
                seat.getSeatNumber(),
                seat.getRoomName(),
                seat.getSeatStatus(),
                seat.getAssignedStudentId(),
                seat.getIsActive(),
                seat.getCreatedAt()
        );
    }

    public static SeatDetailResponse toSeatDetailResponse(Seat seat) {
        return new SeatDetailResponse(
                seat.getSeatId(),
                seat.getSeatNumber(),
                seat.getRoomName(),
                seat.getPositionX(),
                seat.getPositionY(),
                seat.getRotation(),
                seat.getSeatStatus(),
                seat.getHasPowerOutlet(),
                seat.getHasDeskLamp(),
                seat.getHasLocker(),
                seat.getIsNearWindow(),
                seat.getAssignedStudentId(),
                seat.getAssignedAt(),
                seat.getAssignedBy(),
                seat.getIsActive(),
                seat.getCreatedAt(),
                seat.getUpdatedAt()
        );
    }
}
