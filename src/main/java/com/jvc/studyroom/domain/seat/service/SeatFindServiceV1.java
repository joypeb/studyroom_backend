package com.jvc.studyroom.domain.seat.service;

import com.jvc.studyroom.domain.seat.model.Seat;
import com.jvc.studyroom.domain.seat.repository.SeatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;
@Service
@RequiredArgsConstructor
public class SeatFindServiceV1 implements SeatFindService {
    private final SeatRepository seatRepository;
    @Override
    public Mono<UUID> findSeatIdByAssignedStudentId(UUID userId) {
        return seatRepository.findSeatIdByAssignedStudentId(userId);
    }

    @Override
    public Mono<Seat> findByAssignedStudentId(UUID userId) {
        return seatRepository.findByAssignedStudentId(userId);
    }
}

