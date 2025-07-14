package com.jvc.studyroom.domain.seat.service;

import com.jvc.studyroom.common.dto.PaginationRequest;
import com.jvc.studyroom.common.utils.PageableUtil;
import com.jvc.studyroom.domain.seat.converter.SeatMapper;
import com.jvc.studyroom.domain.seat.dto.SeatDetailResponse;
import com.jvc.studyroom.domain.seat.dto.SeatRequest;
import com.jvc.studyroom.domain.seat.dto.SeatResponse;
import com.jvc.studyroom.domain.seat.repository.SeatRepository;
import com.jvc.studyroom.domain.user.converter.UserMapper;
import com.jvc.studyroom.domain.user.dto.UserResponse;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class DefaultSeatService implements SeatService{

    private final SeatRepository seatRepository;
    private final PageableUtil pageableUtil;

    @Override
    public Mono<Page<SeatResponse>> findAllSeats(PaginationRequest request) {
        Pageable pageable = createPageable(request.getPage(), request.getSize(), request.getSortBy(), request.getSortDirection());

        Flux<SeatResponse> seat = seatRepository.findAllBy(pageable).map(SeatMapper::toSeatResponse);
        Mono<Long> count = seatRepository.countAllBy();

        return pageableUtil.createPageResponse(seat, count, pageable);
    }

    @Override
    public Mono<SeatDetailResponse> findSeatById(UUID seatId) {
        return seatRepository.findSeatBySeatId(seatId).map(SeatMapper::toSeatDetailResponse);
    }

    @Override
    public Mono<Void> createSeat(SeatRequest request) {
        UUID seatId = UUID.randomUUID();
        OffsetDateTime now = OffsetDateTime.now();
        return seatRepository.createSeat(SeatMapper.toSeat(request, now, seatId));
    }

    private Pageable createPageable(Integer page, Integer size, String sortBy, String sortDirection) {
        return pageableUtil.createPageable(page, size, sortBy, sortDirection);
    }
}
