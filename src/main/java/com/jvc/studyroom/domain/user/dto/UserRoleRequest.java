package com.jvc.studyroom.domain.user.dto;

import com.jvc.studyroom.common.dto.PaginationRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class UserRoleRequest extends PaginationRequest {
    private String role;
}
