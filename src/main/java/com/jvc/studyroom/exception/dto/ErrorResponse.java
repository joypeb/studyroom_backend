package com.jvc.studyroom.exception.dto;

import com.jvc.studyroom.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class ErrorResponse {
    private String message;
    private LocalDateTime timestamp;
    private String path;

    public static ErrorResponse of(ErrorCode errorCode, String path) {
        return ErrorResponse.builder()
                .message(errorCode.getMessage())
                .timestamp(LocalDateTime.now())
                .path(path)
                .build();
    }
}