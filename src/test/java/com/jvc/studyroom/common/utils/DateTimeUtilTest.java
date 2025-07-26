package com.jvc.studyroom.common.utils;

import com.jvc.studyroom.exception.ErrorCode;
import com.jvc.studyroom.exception.customExceptions.StudyroomServiceException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("DateTimeUtil 테스트")
class DateTimeUtilTest {

    @Nested //관련된 테스트들을 논리적으로 그룹화
    @DisplayName("formatMinutes 메서드 테스트")
    class FormatMinutesTest {


        @Test
        @DisplayName("0분일 때 '0m'을 반환한다")
        void shouldReturn0mWhenMinutesIsZero() {
            // given
            long minutes = 0;
            // when
            String result = DateTimeUtil.formatMinutes(minutes);
            // then
            assertThat(result).isEqualTo("0m");
        }

        @ParameterizedTest
        @DisplayName("1시간 미만일 때 분만 표시한다")
        @ValueSource(longs = {1, 25, 40, 59})
        void shouldReturnOnlyMinutesWhenLessThanOneHour(long minutes) {
            // when
            String result = DateTimeUtil.formatMinutes(minutes);
            // then
            assertThat(result).isEqualTo(minutes + "m");
        }

        @ParameterizedTest
        @DisplayName("정확히 시간 단위일 때 올바른 형식을 반환한다")
        @CsvSource({
                "60, '1h0m'",
                "120, '2h0m'",
                "180, '3h0m'",
                "240, '4h0m'"
        })
        void shouldReturnCorrectFormatWhenExactHours(long minutes, String expected) {
            // when
            String result = DateTimeUtil.formatMinutes(minutes);
            // then
            assertThat(result).isEqualTo(expected);
        }

        @ParameterizedTest
        @DisplayName("시간과 분이 모두 있을 때 올바른 형식을 반환한다")
        @CsvSource({
                "61, '1h1m'",
                "75, '1h15m'",
                "90, '1h30m'",
                "125, '2h5m'",
                "150, '2h30m'",
                "185, '3h5m'"
        })
        void shouldReturnCorrectFormatWhenHoursAndMinutes(long minutes, String expected) {
            // when
            String result = DateTimeUtil.formatMinutes(minutes);

            // then
            assertThat(result).isEqualTo(expected);
        }

        @Test
        @DisplayName("큰 숫자일 때도 올바르게 처리한다")
        void shouldHandleLargeNumbers() {
            // given
            long minutes = 1440; // 24시간

            // when
            String result = DateTimeUtil.formatMinutes(minutes);

            // then
            assertThat(result).isEqualTo("24h0m");
        }

        @Test
        @DisplayName("경계값 테스트 - 59분")
        void shouldHandleBoundaryValue59Minutes() {
            // given
            long minutes = 59;
            // when
            String result = DateTimeUtil.formatMinutes(minutes);
            // then
            assertThat(result).isEqualTo("59m");
        }

        @Test
        @DisplayName("경계값 테스트 - 60분")
        void shouldHandleBoundaryValue60Minutes() {
            // given
            long minutes = 60;

            // when
            String result = DateTimeUtil.formatMinutes(minutes);

            // then
            assertThat(result).isEqualTo("1h0m");
        }

        @Test
        @DisplayName("경계값 테스트 - 61분")
        void shouldHandleBoundaryValue61Minutes() {
            // given
            long minutes = 61;

            // when
            String result = DateTimeUtil.formatMinutes(minutes);

            // then
            assertThat(result).isEqualTo("1h1m");
        }
    }

    @Nested
    @DisplayName("예외 상황 테스트")
    class ExceptionTest {

        @Test
        @DisplayName("음수 입력시 올바른 에러 코드와 메시지를 가진 예외를 던진다")
        void shouldThrowCorrectExceptionForNegativeInput() {
            // given
            long negativeMinutes = -5;

            // when & then
            StudyroomServiceException exception = assertThrows(
                    StudyroomServiceException.class,
                    () -> DateTimeUtil.formatMinutes(negativeMinutes)
            );

            assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.MINUTES_CANNOT_BE_NEGATIVE);
            assertThat(exception.getMessage()).isEqualTo(ErrorCode.MINUTES_CANNOT_BE_NEGATIVE.getMessage());
        }
    }
}