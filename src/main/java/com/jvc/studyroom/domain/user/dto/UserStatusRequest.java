package com.jvc.studyroom.domain.user.dto;

import com.jvc.studyroom.common.enums.AccountStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserStatusRequest {
    private AccountStatus accountStatus;
}
