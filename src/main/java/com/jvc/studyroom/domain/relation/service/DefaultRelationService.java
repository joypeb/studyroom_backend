package com.jvc.studyroom.domain.relation.service;

import com.jvc.studyroom.common.dto.PaginationRequest;
import com.jvc.studyroom.common.utils.PageableUtil;
import com.jvc.studyroom.domain.relation.converter.RelationMapper;
import com.jvc.studyroom.domain.relation.dto.RelationDetailResponse;
import com.jvc.studyroom.domain.relation.dto.RelationResponse;
import com.jvc.studyroom.domain.relation.repository.RelationRepository;
import com.jvc.studyroom.domain.user.model.User;
import com.jvc.studyroom.domain.user.repository.UserRepository;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class DefaultRelationService implements RelationService {

    private final RelationRepository relationRepository;
    private final PageableUtil pageableUtil;
    private final UserRepository userRepository;

    @Override
    public Mono<Page<RelationResponse>> findAllRelations(PaginationRequest request) {
        Pageable pageable = pageableUtil.createPageable(request);

        Flux<RelationResponse> relations = relationRepository.findAll()
                .collectList()
                .flatMapMany(relationList -> {
                    if (relationList.isEmpty()) {
                        return pageableUtil.<RelationResponse>createEmptyPageResponse(pageable)
                                .flatMapMany(page -> Flux.fromIterable(page.getContent()));
                    }

                    List<UUID> userIds = relationList.stream()
                            .flatMap(relation -> Stream.of(relation.getStudentId(), relation.getParentId()))
                            .distinct()
                            .toList();

                    return userRepository.findByUserIdIn(userIds)
                            .collectMap(User::getUserId, Function.identity())
                            .flatMapMany(userMap ->
                                    Flux.fromIterable(relationList)
                                            .map(relation -> RelationMapper.toRelationResponse(relation, userMap)));
                });
        Mono<Long> count = relationRepository.count();

        return pageableUtil.createPageResponse(relations, count, pageable);
    }

    @Override
    public Mono<RelationDetailResponse> findRelationById(UUID relationId) {
        return relationRepository.findByRelationId(relationId)
                .switchIfEmpty(Mono.error(new Exception("해당 관계가 존재하지 않습니다")))
                .flatMap(relation -> {
                    List<UUID> userIds = List.of(relation.getStudentId(), relation.getParentId());

                    return userRepository.findByUserIdIn(userIds)
                            .collectMap(User::getUserId, Function.identity())
                            .map(userMap -> RelationMapper.toRelationDetailResponse(relation, userMap));
                });
    }
}
