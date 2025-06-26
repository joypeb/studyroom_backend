package com.jvc.studyroom.domain.user.repository;

import com.jvc.studyroom.domain.user.model.User;
import reactor.core.publisher.Flux;

public interface UserRepository {
    Flux<User> findByName(String name);
}