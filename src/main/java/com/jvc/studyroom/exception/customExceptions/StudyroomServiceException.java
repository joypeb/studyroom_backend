package com.jvc.studyroom.exception.customExceptions;

import com.jvc.studyroom.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
@Getter
public class StudyroomServiceException extends RuntimeException{
    private final ErrorCode errorCode;
    private final String message;

    public StudyroomServiceException(ErrorCode errorCode) {
        this(errorCode, errorCode.getMessage());
    }
}
