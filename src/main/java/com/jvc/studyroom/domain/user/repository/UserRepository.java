package com.jvc.studyroom.domain.user.repository;

import com.jvc.studyroom.common.enums.UserRole;
import com.jvc.studyroom.domain.user.model.User;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserRepository extends R2dbcRepository<User, UUID> {
    Flux<User> findAllByDeletedAtIsNull(Pageable pageable);
    Mono<User> findByUserIdAndDeletedAtIsNull(UUID userId);
    Flux<User> findAllByRoleAndDeletedAtIsNull(UserRole role);
    Flux<User> findAllByRoleAndDeletedAtIsNull(UserRole role, Pageable pageable);
    Mono<Long> countByRoleAndDeletedAtIsNull(UserRole role);
    Mono<Long> countByDeletedAtIsNull();

}