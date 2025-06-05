package com.jvc.studyroom.common.enums;

public enum SubscriptionStatus {
    PENDING,    // 결제 대기
    ACTIVE,     // 활성
    EXPIRED,    // 만료
    EXHAUSTED,  // 시간 소진 (시간제)
    CANCELED,   // 취소
    SUSPENDED   // 일시 정지
}
