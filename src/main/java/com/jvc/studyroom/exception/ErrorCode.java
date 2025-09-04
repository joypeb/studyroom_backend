package com.jvc.studyroom.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum ErrorCode {
  // validate
  NOT_NULL_PARAM(HttpStatus.BAD_REQUEST, "입력 필수 값입니다."),
  INVALID_PARAM_FORMAT(HttpStatus.BAD_REQUEST, "입력 값의 형식이 올바르지 않습니다."),
  INVALID_PARAM_RANGE(HttpStatus.BAD_REQUEST, "입력 값이 허용된 범위를 벗어났습니다."),

  // util 관련 에러
  MINUTES_CANNOT_BE_NEGATIVE(HttpStatus.BAD_REQUEST, "시간은 음수로 입력할 수 없습니다."),

  // 사용자 관련 에러
  USER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."),
  USER_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 존재하는 사용자입니다."),
  INVALID_USER_CREDENTIALS(HttpStatus.UNAUTHORIZED, "사용자 인증 정보가 올바르지 않습니다."),
  USER_ACCOUNT_LOCKED(HttpStatus.FORBIDDEN, "사용자 계정이 잠겨있습니다."),
  USER_ACCOUNT_DEACTIVATED(HttpStatus.FORBIDDEN, "비활성화된 사용자 계정입니다."),
  INVALID_USER_ROLE(HttpStatus.BAD_REQUEST, "사용자의 역할이 올바르지 않습니다."),

  // 좌석 관련 에러
  SEAT_NOT_FOUND(HttpStatus.NOT_FOUND, "좌석을 찾을 수 없습니다."),
  SEAT_NOT_ASSIGNED(HttpStatus.BAD_REQUEST, "학생에게 할당된 좌석이 없습니다."),
  SEAT_ALREADY_OCCUPIED(HttpStatus.CONFLICT, "이미 사용중인 좌석입니다."),
  SEAT_NOT_AVAILABLE(HttpStatus.BAD_REQUEST, "사용할 수 없는 좌석입니다."),
  SEAT_ASSIGNMENT_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "좌석 배정에 실패했습니다."),

  // 부모-학생 관계 관련 에러
  PARENT_STUDENT_RELATION_NOT_FOUND(HttpStatus.NOT_FOUND, "부모-학생 관계를 찾을 수 없습니다."),
  PARENT_STUDENT_RELATION_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 존재하는 부모-학생 관계입니다."),
  INVALID_PARENT_STUDENT_RELATION(HttpStatus.BAD_REQUEST, "올바르지 않은 부모-학생 관계입니다."),
  PARENT_PERMISSION_REQUIRED(HttpStatus.FORBIDDEN, "부모의 승인이 필요합니다."),

  // 세션 관련 에러
  SESSION_BY_USER_NOT_EXIST(HttpStatus.BAD_REQUEST, "해당 유저의 세션은 존재하지 않습니다."),
  SESSION_EXPIRED(HttpStatus.UNAUTHORIZED, "세션이 만료되었습니다."),
  SESSION_INVALID(HttpStatus.UNAUTHORIZED, "유효하지 않은 세션입니다."),
  MULTIPLE_SESSIONS_DETECTED(HttpStatus.CONFLICT, "중복 세션이 감지되었습니다."),

  // 권한 에러
  ACCESS_DENIED(HttpStatus.FORBIDDEN, "접근 권한이 없습니다."),
  INSUFFICIENT_PRIVILEGES(HttpStatus.FORBIDDEN, "권한이 부족합니다."),
  UNAUTHORIZED_ACCESS(HttpStatus.UNAUTHORIZED, "인증되지 않은 접근입니다."),
  ROLE_NOT_FOUND(HttpStatus.NOT_FOUND, "역할을 찾을 수 없습니다."),

  // 시스템 에러
  INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류가 발생했습니다."),
  DATABASE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "데이터베이스 오류가 발생했습니다."),
  EXTERNAL_API_ERROR(HttpStatus.BAD_GATEWAY, "외부 API 호출 중 오류가 발생했습니다."),
  SERVICE_UNAVAILABLE(HttpStatus.SERVICE_UNAVAILABLE, "서비스를 사용할 수 없습니다.");

  private final HttpStatus status;
  private final String message;
}
