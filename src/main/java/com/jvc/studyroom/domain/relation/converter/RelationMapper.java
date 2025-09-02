package com.jvc.studyroom.domain.relation.converter;

import com.jvc.studyroom.common.enums.ApprovalStatus;
import com.jvc.studyroom.domain.relation.dto.RelationDetailResponse;
import com.jvc.studyroom.domain.relation.dto.RelationRequest;
import com.jvc.studyroom.domain.relation.dto.RelationResponse;
import com.jvc.studyroom.domain.relation.entity.StudentParentRelation;
import com.jvc.studyroom.domain.user.model.User;
import java.time.OffsetDateTime;
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


    public static RelationDetailResponse toRelationDetailResponse(StudentParentRelation relation, Map<UUID, User> userMap) {
        User student = userMap.get(relation.getStudentId());
        User parent = userMap.get(relation.getParentId());

        return RelationDetailResponse.builder()
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

    public static StudentParentRelation toRelation(RelationRequest request) {
        StudentParentRelation studentParentRelation = StudentParentRelation.builder()
                .parentId(request.parentId())
                .studentId(request.studentId())
                .relationType(request.relationType())
                .canViewRealtime(request.canViewRealtime())
                .canViewStatistics(request.canViewStatistics())
                .isActive(request.isActive())
                .approvalStatus(request.approvalStatus())
                .approvedBy(request.approvedBy())
                .build();

        if (request.approvalStatus() == ApprovalStatus.APPROVED) {
            studentParentRelation.setApprovedAt(OffsetDateTime.now());
        }

        return studentParentRelation;
    }

    public static StudentParentRelation toUpdateRelation(StudentParentRelation relation, RelationRequest request) {
        if (request.studentId() != null) {
            relation.setStudentId(request.studentId());
        }

        if (request.parentId() != null) {
            relation.setParentId(request.parentId());
        }

        if (request.relationType() != null) {
            relation.setRelationType(request.relationType());
        }

        if (request.canViewRealtime() != null) {
            relation.setCanViewRealtime(request.canViewRealtime());
        }

        if (request.canViewStatistics() != null) {
            relation.setCanViewStatistics(request.canViewStatistics());
        }

        if (request.isActive() != null) {
            relation.setIsActive(request.isActive());
        }

        if (request.approvalStatus() != null) {
            relation.setApprovalStatus(request.approvalStatus());
        }

        if (request.approvedBy() != null) {
            relation.setApprovedBy(request.approvedBy());
        }

        return relation;
    }
}
