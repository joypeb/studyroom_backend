package com.jvc.studyroom.domain.seat.controller;

import com.jvc.studyroom.common.dto.PaginationRequest;
import com.jvc.studyroom.domain.seat.dto.AssignedStudentSeatRequest;
import com.jvc.studyroom.domain.seat.dto.SeatDetailResponse;
import com.jvc.studyroom.domain.seat.dto.SeatRequest;
import com.jvc.studyroom.domain.seat.dto.SeatResponse;
import com.jvc.studyroom.domain.seat.service.SeatService;
import com.jvc.studyroom.domain.user.dto.UserResponse;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/seats")
public class SeatController {

    private final SeatService seatService;

    @GetMapping()
    public Mono<Page<SeatResponse>> getAllSeats(@RequestBody PaginationRequest request) {
        return seatService.findAllSeats(request);
    }

    @GetMapping("/{seatId}")
    public Mono<SeatDetailResponse> getSeatById(@PathVariable UUID seatId) {
        return seatService.findSeatById(seatId);
    }

    @PostMapping()
    public Mono<Void> createSeat(@RequestBody SeatRequest request) {
        return seatService.createSeat(request);
    }

    @PutMapping("/{seatId}/assignment")
    public Mono<Integer> updateAssignedStudentSeatById(@PathVariable UUID seatId, @RequestBody AssignedStudentSeatRequest request) {
        return seatService.updateAssignedStudentSeatById(seatId, request);
    }

    @DeleteMapping("/{seatId}")
    public Mono<Void> deleteSeatById(@PathVariable UUID seatId) {
        return seatService.deleteSeatById(seatId);
    }
}
