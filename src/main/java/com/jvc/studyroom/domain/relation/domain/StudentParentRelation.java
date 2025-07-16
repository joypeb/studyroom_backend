package com.jvc.studyroom.domain.relation.domain;

import com.jvc.studyroom.common.enums.ApprovalStatus;
import com.jvc.studyroom.common.enums.RelationType;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Setter
@Table("student_parent_relations")
public class StudentParentRelation {

    @Id
    private UUID relationId;
    private UUID studentId;
    private UUID parentId;
    private RelationType relationType;
    private Boolean canViewRealtime = true;
    private Boolean canViewStatistics = true;
    private Boolean isActive = true;
    private OffsetDateTime connectedAt;
    private OffsetDateTime disconnectedAt;
    private ApprovalStatus approvalStatus = ApprovalStatus.APPROVED;
    private UUID approvedBy;
    private OffsetDateTime approvedAt;
    @CreatedDate
    private OffsetDateTime createdAt;
    @LastModifiedDate
    private OffsetDateTime updatedAt;
}
