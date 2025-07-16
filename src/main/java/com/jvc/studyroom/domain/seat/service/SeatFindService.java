package com.jvc.studyroom.domain.seat.service;

import com.jvc.studyroom.domain.seat.model.Seat;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface SeatFindService {
    Mono<UUID> findSeatIdByAssignedStudentId(UUID studentId);

    Mono<Seat> findByAssignedStudentId(UUID studentId);
}
