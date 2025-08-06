package com.jvc.studyroom.exception.customExceptions;

import com.jvc.studyroom.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class StudyroomServiceException extends RuntimeException {

  private final ErrorCode errorCode;
  private final String message;

  // 기본 생성자
  public StudyroomServiceException(ErrorCode errorCode) {
    this(errorCode, errorCode.getMessage());
  }

  // cause가 있는 경우 - 기본 메시지
  public StudyroomServiceException(ErrorCode errorCode, Throwable cause) {
    super(errorCode.getMessage(), cause);
    this.errorCode = errorCode;
    this.message = errorCode.getMessage();
  }

  // cause가 있는 경우 - 커스텀 메시지
  public StudyroomServiceException(ErrorCode errorCode, String customMessage, Throwable cause) {
    super(customMessage, cause);
    this.errorCode = errorCode;
    this.message = customMessage;
  }

  /*
   * 로그용 데이터 반환
   */
  public String getLogMessage() {
    StringBuilder sb = new StringBuilder();
    sb.append("[").append(errorCode.name()).append("] ");
    sb.append(message);

    if (getCause() != null) {
      sb.append(" | 원인: ").append(getCause().getClass().getSimpleName());
      if (getCause().getMessage() != null) {
        sb.append(" - ").append(getCause().getMessage());
      }
    }
    return sb.toString();
  }
}
