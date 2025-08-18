package com.jvc.studyroom.common.utils;

import com.jvc.studyroom.exception.ErrorCode;
import com.jvc.studyroom.exception.customExceptions.StudyroomServiceException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DateTimeUtil {
    public static String formatMinutes(long minutes) {
        if (minutes < 0) {
            throw new StudyroomServiceException(ErrorCode.MINUTES_CANNOT_BE_NEGATIVE, ErrorCode.MINUTES_CANNOT_BE_NEGATIVE.getMessage());
        }
        long hours = minutes / 60;
        long mins = minutes % 60;

        if (minutes == 0) {
            return "0m";
        }

        if (hours > 0) {
            return String.format("%dh%dm", hours, mins);
        } else {
            return String.format("%dm", mins);
        }
    }
}
