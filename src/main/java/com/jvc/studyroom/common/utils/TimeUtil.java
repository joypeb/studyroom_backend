package com.jvc.studyroom.common.utils;

import java.time.OffsetDateTime;
import java.time.ZoneId;

/**
 * 시간 관련 유틸리티 클래스 한국시간(KST) 기준으로 일관된 시간 처리
 */
public class TimeUtil {

  /**
   * 한국시간 Zone ID
   */
  public static final ZoneId KOREA_ZONE = ZoneId.of("Asia/Seoul");

  /**
   * 한국시간 기준 현재 시간 반환
   */
  public static OffsetDateTime nowInKorea() {
    return OffsetDateTime.now(KOREA_ZONE);
  }

  /**
   * 주어진 시간을 한국시간으로 변환
   */
  public static OffsetDateTime toKoreaTime(OffsetDateTime dateTime) {
    if (dateTime == null) {
      return null;
    }
    return dateTime.atZoneSameInstant(KOREA_ZONE).toOffsetDateTime();
  }
}
