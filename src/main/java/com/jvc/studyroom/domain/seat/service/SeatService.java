package com.jvc.studyroom.domain.seat.service;

import com.jvc.studyroom.common.dto.PaginationRequest;
import com.jvc.studyroom.domain.seat.dto.SeatDetailResponse;
import com.jvc.studyroom.domain.seat.dto.SeatResponse;
import java.util.UUID;
import org.springframework.data.domain.Page;
import reactor.core.publisher.Mono;

public interface SeatService {
    Mono<Page<SeatResponse>> findAllSeats(PaginationRequest request);
    Mono<SeatDetailResponse> findSeatById(UUID seatId);
}
