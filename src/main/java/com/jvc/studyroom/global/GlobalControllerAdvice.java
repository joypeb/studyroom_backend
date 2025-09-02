package com.jvc.studyroom.global;

import com.jvc.studyroom.common.dto.ApiResponse;
import com.jvc.studyroom.exception.ErrorCode;
import com.jvc.studyroom.exception.customExceptions.StudyroomServiceException;
import com.jvc.studyroom.exception.dto.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ServerWebExchange;

import java.time.LocalDateTime;

@ControllerAdvice
@Slf4j
public class GlobalControllerAdvice {

    @ExceptionHandler(StudyroomServiceException.class)
    public ResponseEntity<ApiResponse<String>> handleStudyroomServiceException(
            StudyroomServiceException ex, ServerWebExchange exchange) {

        log.error("StudyroomServiceException occurred: {}", ex.getMessage(), ex);

        String path = exchange.getRequest().getURI().getPath();


        return ResponseEntity
                .status(ex.getErrorCode().getStatus())
                .body(ApiResponse.error(ex.getErrorCode().getMessage(), path));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(
            IllegalArgumentException ex, ServerWebExchange exchange) {

        log.error("IllegalArgumentException occurred: {}", ex.getMessage(), ex);

        String path = exchange.getRequest().getURI().getPath();
        ErrorResponse errorResponse = ErrorResponse.builder()
                .message("잘못된 요청 파라미터입니다: " + ex.getMessage())
                .timestamp(LocalDateTime.now())
                .path(path)
                .build();

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errorResponse);
    }

    @ExceptionHandler(WebExchangeBindException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            WebExchangeBindException ex, ServerWebExchange exchange) {

        log.error("Validation error occurred: {}", ex.getMessage());

        StringBuilder message = new StringBuilder("입력값 검증 실패: ");
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            message.append(error.getField())
                    .append(" - ")
                    .append(error.getDefaultMessage())
                    .append("; ");
        });

        String path = exchange.getRequest().getURI().getPath();
        ErrorResponse errorResponse = ErrorResponse.builder()
                .message(message.toString())
                .timestamp(LocalDateTime.now())
                .path(path)
                .build();

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneralException(
            Exception ex, ServerWebExchange exchange) {

        log.error("Unexpected error occurred: {}", ex.getMessage(), ex);

        String path = exchange.getRequest().getURI().getPath();
        ErrorResponse errorResponse = ErrorResponse.of(
                ErrorCode.INTERNAL_SERVER_ERROR,
                path
        );

        return ResponseEntity
                .status(ErrorCode.INTERNAL_SERVER_ERROR.getStatus())
                .body(errorResponse);
    }
}
