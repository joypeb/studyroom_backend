package com.jvc.studyroom.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum ErrorCode {
  // validate
  NOT_NULL_PARAM(HttpStatus.BAD_REQUEST, "입력 필수 값입니다."),
  // util 관련 에러
  MINUTES_CANNOT_BE_NEGATIVE(HttpStatus.BAD_REQUEST, "시간은 음수로 입력할 수 없습니다."),
  // 서비스 에러
  SEAT_NOT_ASSIGNED(HttpStatus.BAD_REQUEST, "학생에게 할당된 좌석이 없습니다."),
  SESSION_BY_USER_NOT_EXIST(HttpStatus.BAD_REQUEST, "해당 유저의 세션은 존재하지 않습니다."),
  // 시스템 에러
  INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류가 발생했습니다."),
  DATABASE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "데이터베이스 오류가 발생했습니다.");

  private final HttpStatus status;
  private final String message;
}
