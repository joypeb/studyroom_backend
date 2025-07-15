package com.jvc.studyroom.domain.user.converter;

import com.jvc.studyroom.domain.user.dto.UserResponse;
import com.jvc.studyroom.domain.user.dto.UserUpdateRequest;
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

    public static User toUpdateUser(User user, UserUpdateRequest request) {
        if (request.email() != null) {
            user.setEmail(request.email());
        }

        if (request.name() != null) {
            user.setName(request.name());
        }

        if (request.phoneNumber() != null) {
            user.setPhoneNumber(request.phoneNumber());
        }

        if (request.accountStatus() != null) {
            user.setAccountStatus(request.accountStatus());
        }

        if (request.role() != null) {
            user.setRole(request.role());
        }

        if (request.assignedSeatId() != null) {
            user.setAssignedSeatId(request.assignedSeatId());
        }

        return user;
    }
}