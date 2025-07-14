package com.jvc.studyroom.domain.seat.repository;

import com.jvc.studyroom.domain.seat.model.Seat;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface SeatRepository extends R2dbcRepository<Seat, UUID> {
    Flux<Seat> findAllBy(Pageable pageable);
    Mono<Long> countAllBy();
    Mono<Seat> findSeatBySeatId(UUID seatId);

    @Query("""
        INSERT INTO seats (
            seat_id, seat_number, room_name, position_x, position_y, rotation,
            seat_status, has_power_outlet, has_desk_lamp, has_locker, is_near_window,
            assigned_student_id, assigned_at, assigned_by, is_active, created_at, updated_at
        ) VALUES (
            :#{#seat.seatId}, :#{#seat.seatNumber}, :#{#seat.roomName}, :#{#seat.positionX}, :#{#seat.positionY}, :#{#seat.rotation},
            :#{#seat.seatStatus}, :#{#seat.hasPowerOutlet}, :#{#seat.hasDeskLamp}, :#{#seat.hasLocker}, :#{#seat.isNearWindow},
            :#{#seat.assignedStudentId}, :#{#seat.assignedAt}, :#{#seat.assignedBy}, :#{#seat.isActive}, :#{#seat.createdAt}, :#{#seat.updatedAt}
        )
        """)
    Mono<Void> createSeat(Seat seat);
}
