package com.jvc.studyroom.domain.relation.repository;

import com.jvc.studyroom.domain.relation.entity.StudentParentRelation;
import java.util.UUID;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface RelationRepository extends R2dbcRepository<StudentParentRelation, UUID> {

    Mono<StudentParentRelation> findByRelationId(UUID relationId);
    Flux<StudentParentRelation> findAllByParentId(UUID parentId);
    Flux<StudentParentRelation> findAllByStudentId(UUID studentId);
}
