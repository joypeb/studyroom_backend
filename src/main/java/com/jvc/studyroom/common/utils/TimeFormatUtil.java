package com.jvc.studyroom.common.utils;

/**
 * 시간 포맷팅 관련 유틸리티
 */
public class TimeFormatUtil {
    
    /**
     * 분을 "1시간 25분" 형태로 포맷
     */
    public static String formatStudyTime(long totalMinutes) {
        if (totalMinutes <= 0) {
            return "0분";
        }

        long hours = totalMinutes / 60;
        long minutes = totalMinutes % 60;

        if (hours == 0) {
            return minutes + "분";
        } else if (minutes == 0) {
            return hours + "시간";
        } else {
            return hours + "시간 " + minutes + "분";
        }
    }
}
