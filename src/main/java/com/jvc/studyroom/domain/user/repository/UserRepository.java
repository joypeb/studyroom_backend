package com.jvc.studyroom.domain.user.repository;

import com.jvc.studyroom.domain.user.model.User;
import java.util.UUID;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserRepository extends R2dbcRepository<User, UUID> {
    Flux<User> findAllByDeletedAtIsNull();
    Mono<User> findByUserIdAndDeletedAtIsNull(UUID userId);
    Mono<Long> countByEmailAndDeletedAtIsNull(String email);
    Mono<User> findByEmailAndDeletedAtIsNull(String email);
}