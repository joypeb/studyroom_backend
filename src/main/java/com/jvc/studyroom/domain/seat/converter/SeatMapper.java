package com.jvc.studyroom.domain.seat.converter;

import com.jvc.studyroom.domain.seat.dto.SeatDetailResponse;
import com.jvc.studyroom.domain.seat.dto.SeatRequest;
import com.jvc.studyroom.domain.seat.dto.SeatResponse;
import com.jvc.studyroom.domain.seat.model.Seat;
import java.time.OffsetDateTime;
import java.util.UUID;

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

    public static Seat toSeat(SeatRequest request, OffsetDateTime now, UUID seatId) {
        return Seat.builder()
                .seatId(seatId)
                .seatNumber(request.seatNumber())
                .roomName(request.roomName())
                .positionX(request.positionX())
                .positionY(request.positionY())
                .rotation(request.rotation())
                .seatStatus(request.seatStatus())
                .hasPowerOutlet(request.hasPowerOutlet())
                .hasDeskLamp(request.hasDeskLamp())
                .hasLocker(request.hasLocker())
                .isNearWindow(request.isNearWindow())
                .assignedStudentId(null)
                .assignedAt(null)
                .assignedBy(null)
                .isActive(request.isActive())
                .createdAt(now)
                .updatedAt(now)
                .build();
    }
}
