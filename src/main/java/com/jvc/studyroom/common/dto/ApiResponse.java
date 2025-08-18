package com.jvc.studyroom.common.dto;

import java.time.OffsetDateTime;

/**
 * API 응답 래퍼
 */
public record ApiResponse<T>(
    boolean success,
    String message,
    T data,
    OffsetDateTime timestamp
) {

  public static <T> ApiResponse<T> success(T data) {
    return new ApiResponse<>(true, "성공", data, OffsetDateTime.now());
  }

  public static <T> ApiResponse<T> success(String message, T data) {
    return new ApiResponse<>(true, message, data, OffsetDateTime.now());
  }

  public static <T> ApiResponse<T> error(String message) {
    return new ApiResponse<>(false, message, null, OffsetDateTime.now());
  }
}