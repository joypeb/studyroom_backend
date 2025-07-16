package com.jvc.studyroom.common.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class PaginationRequest {
    private int page;
    private int size;
    private String sortBy;
    private String sortDirection;
}
