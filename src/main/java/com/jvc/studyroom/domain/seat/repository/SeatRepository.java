package com.jvc.studyroom.domain.seat.repository;

import com.jvc.studyroom.domain.seat.entity.Seat;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface SeatRepository extends R2dbcRepository<Seat, UUID> {

    /*
    특정 학생이 배정된 좌석 조회
     */
    // return Seat
    Mono<Seat> findByAssignedStudentId(UUID assignedStudentId);
    // return seatId
    @Query("SELECT seat_id FROM seats WHERE assigned_student_id = :studentId")
    Mono<UUID> findSeatIdByAssignedStudentId(UUID studentId);
}