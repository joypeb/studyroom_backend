package com.jvc.studyroom.domain.user.model;

import com.jvc.studyroom.common.enums.AccountStatus;
import com.jvc.studyroom.common.enums.UserRole;
import java.time.OffsetDateTime;
import java.util.UUID;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Setter // Setter 안 쓰고 생성자 기법 쓸까 하고 있음
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table("users")
public class User {
    @Id
    private UUID userId;
    private String email;
    private String name;
    private String phoneNumber;
    private String username;
    private String password;
    private AccountStatus accountStatus = AccountStatus.ACTIVE;
    private OffsetDateTime lastLoginAt;
    private Integer failedLoginAttempts = 0;
    private OffsetDateTime accountLockedUntil;
    @CreatedDate
    private OffsetDateTime createdAt;
    @LastModifiedDate
    private OffsetDateTime updatedAt;
    private OffsetDateTime deletedAt;
    private UserRole role = UserRole.NONE;
    private UUID assignedSeatId;
}
