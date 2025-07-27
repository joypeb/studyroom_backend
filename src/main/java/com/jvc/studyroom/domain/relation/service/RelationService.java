package com.jvc.studyroom.domain.relation.service;

import com.jvc.studyroom.common.dto.PaginationRequest;
import com.jvc.studyroom.domain.relation.dto.RelationDetailResponse;
import com.jvc.studyroom.domain.relation.dto.RelationRequest;
import com.jvc.studyroom.domain.relation.dto.RelationResponse;
import java.util.UUID;
import org.springframework.data.domain.Page;
import reactor.core.publisher.Mono;

public interface RelationService {
    Mono<Page<RelationResponse>> findAllRelations(PaginationRequest request);
    Mono<RelationDetailResponse> findRelationById(UUID relationId);
    Mono<Void> createRelation(RelationRequest request);
    Mono<RelationDetailResponse> updateRelation(UUID relationId, RelationRequest request);
    Mono<Void> deleteRelation(UUID relationId);
}
