package com.jvc.studyroom.domain.user.dto;

public record TokenResponse(
        String accessToken,
        String refreshToken) {
}
