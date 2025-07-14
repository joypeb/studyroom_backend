package com.jvc.studyroom.domain.seat.service;

import com.jvc.studyroom.common.dto.PaginationRequest;
import com.jvc.studyroom.common.enums.AccountStatus;
import com.jvc.studyroom.common.enums.SeatStatus;
import com.jvc.studyroom.common.enums.UserRole;
import com.jvc.studyroom.common.utils.PageableUtil;
import com.jvc.studyroom.domain.seat.converter.SeatMapper;
import com.jvc.studyroom.domain.seat.dto.AssignedStudentSeatRequest;
import com.jvc.studyroom.domain.seat.dto.SeatDetailResponse;
import com.jvc.studyroom.domain.seat.dto.SeatRequest;
import com.jvc.studyroom.domain.seat.dto.SeatResponse;
import com.jvc.studyroom.domain.seat.repository.SeatRepository;
import com.jvc.studyroom.domain.user.repository.UserRepository;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

@Service
@RequiredArgsConstructor
@Slf4j
public class DefaultSeatService implements SeatService {

    private final SeatRepository seatRepository;
    private final UserRepository userRepository;
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
        return seatRepository.createSeat(SeatMapper.toSeat(request, seatId));
    }

    @Override
    public Mono<Integer> updateAssignedStudentSeatById(UUID seatId, AssignedStudentSeatRequest request) {
        return userRepository.findByUserIdAndAccountStatus(request.studentId(), AccountStatus.ACTIVE)
                .switchIfEmpty(Mono.error(new Exception("해당 사용자가 존재하지 않습니다")))
                .filter(user -> user.getRole().equals(UserRole.STUDENT))
                .switchIfEmpty(Mono.error(new Exception("학생이 아닙니다")))
                .then(seatRepository.findSeatBySeatId(seatId))
                .switchIfEmpty(Mono.error(new Exception("해당 좌석이 존재하지 않습니다")))
                .filter(seat -> seat.getSeatStatus().equals(SeatStatus.AVAILABLE))
                .switchIfEmpty(Mono.error(new Exception("해당 좌석이 이미 사용중입니다")))
                .then(userRepository.updateAssignedSeatId(request.studentId(), seatId))
                .then(seatRepository.updateAssignedStudentSeatById(
                        seatId, request.studentId(), request.studentId(), SeatStatus.OCCUPIED));
    }

    private Pageable createPageable(Integer page, Integer size, String sortBy, String sortDirection) {
        return pageableUtil.createPageable(page, size, sortBy, sortDirection);
    }
}
