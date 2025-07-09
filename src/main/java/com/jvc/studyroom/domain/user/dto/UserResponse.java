package com.jvc.studyroom.domain.user.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.jvc.studyroom.common.enums.UserRole;
import java.time.OffsetDateTime;
import java.util.UUID;

public record UserResponse(
        UUID userId,
        String email,
        String name,
        String phoneNumber,
        String accountStatus,
        UserRole role,
        UUID assignedSeatId,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
        OffsetDateTime lastLoginAt,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
        OffsetDateTime createdAt) {
}
