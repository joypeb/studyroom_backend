package com.jvc.studyroom.common.enums;

import lombok.Getter;

@Getter
public enum RelationType {
    MOTHER("어머니"),
    FATHER("아버지");

    private final String description;

    RelationType(String description) {
        this.description = description;
    }
}
