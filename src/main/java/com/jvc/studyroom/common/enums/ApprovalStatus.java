package com.jvc.studyroom.common.enums;

import lombok.Getter;

@Getter
public enum ApprovalStatus {
    PENDING("승인 대기"),
    APPROVED("승인됨"),
    REJECTED("거절됨");

    private final String description;

    ApprovalStatus(String description) {
        this.description = description;
    }
}
