package com.jvc.studyroom.domain.user.converter;

import com.jvc.studyroom.domain.user.dto.UserRequest;
import com.jvc.studyroom.domain.user.dto.UserResponse;
import com.jvc.studyroom.domain.user.model.User;

public class UserMapper {
    public static User toEntity(UserRequest dto) {
        User user = new User();
        user.setName(dto.name());
        user.setEmail(dto.email());
        return user;
    }

    public static UserResponse toDto(User user) {
        return new UserResponse(user.getId(), user.getName(), user.getEmail());
    }
}