package com.jvc.studyroom.domain.relation.converter;

import com.jvc.studyroom.domain.relation.dto.RelationResponse;
import com.jvc.studyroom.domain.relation.entity.StudentParentRelation;
import com.jvc.studyroom.domain.user.model.User;
import java.util.Map;
import java.util.UUID;

public class RelationMapper {

    public static RelationResponse toRelationResponse(StudentParentRelation relation, Map<UUID, User> userMap) {
        User student = userMap.get(relation.getStudentId());
        User parent = userMap.get(relation.getParentId());

        return RelationResponse.builder()
                .relationId(relation.getRelationId())
                .studentId(relation.getStudentId())
                .studentName(student != null ? student.getName() : "Unknown Student")
                .parentId(relation.getParentId())
                .parentsName(parent != null ? parent.getName() : "Unknown Parent")
                .relationType(relation.getRelationType())
                .relationTypeName(relation.getRelationType().getDescription())
                .canViewRealtime(relation.getCanViewRealtime())
                .canViewStatistics(relation.getCanViewStatistics())
                .isActive(relation.getIsActive())
                .connectedAt(relation.getConnectedAt())
                .disconnectedAt(relation.getDisconnectedAt())
                .approvalStatus(relation.getApprovalStatus())
                .approvedBy(relation.getApprovedBy())
                .approvedAt(relation.getApprovedAt())
                .createdAt(relation.getCreatedAt())
                .updatedAt(relation.getUpdatedAt())
                .build();
    }
}
