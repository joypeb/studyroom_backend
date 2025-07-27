package com.jvc.studyroom.domain.relation.dto;


import com.jvc.studyroom.common.enums.ApprovalStatus;
import com.jvc.studyroom.common.enums.RelationType;
import java.time.OffsetDateTime;
import java.util.UUID;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

public record RelationRequest(
    UUID studentId,
    UUID parentId,
    RelationType relationType,
    Boolean canViewRealtime,
    Boolean canViewStatistics,
    Boolean isActive,
    ApprovalStatus approvalStatus,
    UUID approvedBy
) {}
