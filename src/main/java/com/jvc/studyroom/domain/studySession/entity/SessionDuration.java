package com.jvc.studyroom.domain.studySession.entity;

/**
 * 세션 지속시간 Enum
 */
public enum SessionDuration {
  ONE_HOUR(1, "1시간"),
  TWO_HOURS(2, "2시간"),
  THREE_HOURS(3, "3시간"),
  FOUR_HOURS(4, "4시간");

  private final int hours;
  private final String displayName;

  SessionDuration(int hours, String displayName) {
    this.hours = hours;
    this.displayName = displayName;
  }

  public int getHours() {
    return hours;
  }

  public String getDisplayName() {
    return displayName;
  }

  public long getMinutes() {
    return hours * 60L;
  }
}
