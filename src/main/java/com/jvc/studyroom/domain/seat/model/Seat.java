package com.jvc.studyroom.domain.seat.model;

import com.jvc.studyroom.common.enums.SeatStatus;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.relational.core.mapping.Table;

@Table("seats")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Seat {
    @Id
    private UUID seatId;

    private String seatNumber;

    private String roomName;

    private Integer positionX;

    private Integer positionY;

    private Integer rotation;

    private SeatStatus seatStatus = SeatStatus.AVAILABLE;

    private Boolean hasPowerOutlet = true;

    private Boolean hasDeskLamp = true;

    private Boolean hasLocker = true;

    private Boolean isNearWindow = false;

    private UUID assignedStudentId;

    private OffsetDateTime assignedAt;

    private UUID assignedBy;

    private Boolean isActive = true;

    @CreatedDate
    private OffsetDateTime createdAt;

    @LastModifiedDate
    private OffsetDateTime updatedAt;
}
