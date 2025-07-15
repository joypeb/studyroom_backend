package com.jvc.studyroom.domain.seat.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@NoArgsConstructor
@Table("seats")
public class Seat {

    @Id
    private UUID seatId;

    private String seatNumber;
    private String roomName;

    private Integer positionX;
    private Integer positionY;
    private Integer rotation;

    private SeatStatus seatStatus; // AVAILABLE, OCCUPIED 등

    private Boolean hasPowerOutlet;
    private Boolean hasDeskLamp;
    private Boolean hasLocker;
    private Boolean isNearWindow;

    private UUID assignedStudentId;
    private OffsetDateTime assignedAt;

    private UUID assignedBy;

    private Boolean isActive;

    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

}
