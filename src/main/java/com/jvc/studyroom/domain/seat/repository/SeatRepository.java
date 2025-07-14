package com.jvc.studyroom.domain.seat.repository;

import com.jvc.studyroom.domain.seat.model.Seat;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface SeatRepository extends R2dbcRepository<Seat, UUID> {
    Flux<Seat> findAllBy(Pageable pageable);
    Mono<Long> countAllBy();
    Mono<Seat> findSeatBySeatId(UUID seatId);
}
