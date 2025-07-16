package com.jvc.studyroom.domain.seat.dto;

import java.util.UUID;

public record AssignedStudentSeatRequest(
        UUID studentId
) {
}
