package com.jvc.studyroom.domain.user.repository;

import com.jvc.studyroom.domain.user.model.User;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface UserRepository extends R2dbcRepository<User, UUID> {
    Mono<User> findUserByUserId(UUID userId);
}
