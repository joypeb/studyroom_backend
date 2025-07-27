package com.jvc.studyroom.domain.relation.dto;

import com.jvc.studyroom.common.enums.ApprovalStatus;
import com.jvc.studyroom.common.enums.RelationType;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RelationDetailResponse {
    private UUID relationId;
    private UUID studentId;
    private String studentName;
    private UUID parentId;
    private String parentsName;
    private RelationType relationType;
    private String relationTypeName;
    private Boolean canViewRealtime;
    private Boolean canViewStatistics;
    private Boolean isActive;
    private OffsetDateTime connectedAt;
    private OffsetDateTime disconnectedAt;
    private ApprovalStatus approvalStatus;
    private UUID approvedBy;
    private OffsetDateTime approvedAt;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
