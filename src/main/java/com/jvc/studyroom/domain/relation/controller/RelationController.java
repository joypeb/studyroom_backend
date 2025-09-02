package com.jvc.studyroom.domain.relation.controller;

import com.jvc.studyroom.common.dto.PaginationRequest;
import com.jvc.studyroom.domain.relation.dto.RelationDetailResponse;
import com.jvc.studyroom.domain.relation.dto.RelationRequest;
import com.jvc.studyroom.domain.relation.dto.RelationResponse;
import com.jvc.studyroom.domain.relation.service.RelationService;
import com.jvc.studyroom.domain.seat.dto.SeatDetailResponse;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/relations")
public class RelationController {

    private final RelationService relationService;

    // 모든 학생 부모 관계 리스트
    @GetMapping()
    public Mono<Page<RelationResponse>> getAllRelations(@RequestBody PaginationRequest request) {
        return relationService.findAllRelations(request);
    }

    // 특정 관계 확인
    @GetMapping("/{relationId}")
    public Mono<RelationDetailResponse> getRelationById(@PathVariable UUID relationId) {
        return relationService.findRelationById(relationId);
    }

    // 관계 추가
    @PostMapping()
    public Mono<Void> createRelation(@RequestBody RelationRequest request) {
        return relationService.createRelation(request);
    }

    // 관계 수정
    @PutMapping("/{relationId}")
    public Mono<RelationDetailResponse> updateRelation(@PathVariable UUID relationId, @RequestBody RelationRequest request) {
        return relationService.updateRelation(relationId, request);
    }

    // 관계 삭제
    @DeleteMapping("/{relationId}")
    public Mono<Void> deleteRelation(@PathVariable UUID relationId) {
        return relationService.deleteRelation(relationId);
    }
}
