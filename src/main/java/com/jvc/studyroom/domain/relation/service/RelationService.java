package com.jvc.studyroom.domain.relation.service;

import com.jvc.studyroom.common.dto.PaginationRequest;
import com.jvc.studyroom.domain.relation.dto.RelationResponse;
import org.springframework.data.domain.Page;
import reactor.core.publisher.Mono;

public interface RelationService {
    Mono<Page<RelationResponse>> findAllRelations(PaginationRequest request);
}
