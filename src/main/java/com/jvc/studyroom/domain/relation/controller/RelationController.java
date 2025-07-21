package com.jvc.studyroom.domain.relation.controller;

import com.jvc.studyroom.common.dto.PaginationRequest;
import com.jvc.studyroom.domain.relation.dto.RelationResponse;
import com.jvc.studyroom.domain.relation.service.RelationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
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
    // 특정 부모에 대한 학생 리스트
    // 특정 학생에 대한 부모 리스트
    // 관계 추가
    // 관계 수정
    // 관계 삭제
}
