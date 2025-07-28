package com.jvc.studyroom.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum ErrorCode {
    // util 관련 에러
    MINUTES_CANNOT_BE_NEGATIVE(HttpStatus.BAD_REQUEST, "Minutes cannot be negative"),

    // 시스템 에러
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류가 발생했습니다."),
    DATABASE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "데이터베이스 오류가 발생했습니다." );

    private final HttpStatus status;
    private final String message;
}
