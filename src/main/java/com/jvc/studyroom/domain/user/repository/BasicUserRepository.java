package com.jvc.studyroom.domain.user.repository;
import com.jvc.studyroom.domain.user.model.User;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public class BasicUserRepository implements UserRepository {
    @Override
    public Flux<User> findByName(String name) {
        return null;
    }
}
