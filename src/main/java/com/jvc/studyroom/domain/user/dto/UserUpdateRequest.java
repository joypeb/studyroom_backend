package com.jvc.studyroom.domain.user.dto;

import com.jvc.studyroom.common.enums.AccountStatus;
import com.jvc.studyroom.common.enums.UserRole;
import java.util.UUID;

public record UserUpdateRequest(
        String email,
        String name,
        String phoneNumber,
        AccountStatus accountStatus,
        UserRole role,
        UUID assignedSeatId
) {

}
