package com.jvc.studyroom.domain.studySession.dto;

import com.jvc.studyroom.domain.studySession.entity.SessionDuration;
import com.jvc.studyroom.exception.ErrorCode;
import com.jvc.studyroom.exception.customExceptions.StudyroomServiceException;

/**
 * 스터디 세션 생성 요청 plannedEndTime 대신 SessionDuration으로 입력받아 자동 계산
 */
public record SessionCreateRequest(
    SessionDuration duration  // 세션 지속시간은 필수
) {

  /**
   * duration이 null인지 검증
   */
  public SessionCreateRequest {
    if (duration == null) {
      throw new StudyroomServiceException(ErrorCode.NOT_NULL_PARAM, "세션 지속시간은 필수입니다.");
    }
  }
}
