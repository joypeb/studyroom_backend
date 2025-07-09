package com.jvc.studyroom.domain.user.converter;

import com.jvc.studyroom.domain.user.dto.UserResponse;
import com.jvc.studyroom.domain.user.model.User;

public class UserMapper {
    public static UserResponse toUserResponse(User user) {
        return new UserResponse(
                user.getUserId(),
                user.getEmail(),
                user.getName(),
                user.getPhoneNumber(),
                user.getAccountStatus().name(),
                user.getRole(),
                user.getAssignedSeatId(),
                user.getLastLoginAt(),
                user.getCreatedAt());
    }
}