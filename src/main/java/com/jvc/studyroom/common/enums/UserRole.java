package com.jvc.studyroom.common.enums;

public enum UserRole {
    NONE,
    STUDENT,
    PARENTS,
    ADMIN,
    SUPER_ADMIN;

    public static UserRole fromString(String role) {
        if (role == null || role.trim().isEmpty()) {
            return null;
        }

        try {
            return UserRole.valueOf(role.toUpperCase().trim());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
