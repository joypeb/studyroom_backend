package com.jvc.studyroom.domain.relation.repository;

import com.jvc.studyroom.domain.relation.entity.StudentParentRelation;
import java.util.UUID;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.query.Param;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface RelationRepository extends R2dbcRepository<StudentParentRelation, UUID> {

    Mono<StudentParentRelation> findByRelationId(UUID relationId);
    Flux<StudentParentRelation> findAllByParentId(UUID parentId);
    Flux<StudentParentRelation> findAllByStudentId(UUID studentId);
    Mono<Boolean> existsByParentIdAndStudentId(UUID parentId, UUID studentId);
    @Query("UPDATE student_parent_relations SET is_active = false, updated_at = NOW() WHERE relation_id = :relationId")
    Mono<Void> deActiveRelation(@Param("relationId") UUID relationId);
}
