package com.jvc.studyroom.domain.studySession.dto;

import java.time.OffsetDateTime;

/*
 email은 로그인 기능 다 구현 되면 세션에 있는 로그인 토큰 이용하는 것으로 변경
 */
public record SessionCreateRequest(
        OffsetDateTime plannedEndTime
                                   )
{
}
