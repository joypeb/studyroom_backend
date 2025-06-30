package com.jvc.studyroom.domain.user.repository;

import com.jvc.studyroom.domain.user.model.BaseUser;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface UserRepository extends ReactiveCrudRepository<BaseUser, Long> {
    Mono<BaseUser> findByUserId(String UserId);
}