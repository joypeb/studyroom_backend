package com.jvc.studyroom.domain.user.service;

import com.jvc.studyroom.domain.user.converter.UserMapper;
import com.jvc.studyroom.domain.user.dto.UserResponse;
import com.jvc.studyroom.domain.user.repository.UserRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class DefaultUserService implements UserService {

    private final UserRepository userRepository;

    @Override
    public Flux<UserResponse> findAllUsers(){
        return userRepository.findAllByDeletedAtIsNull().map(UserMapper::toUserResponse);
    }

    @Override
    public Mono<UserResponse> findUserById(UUID userId) {
        return userRepository.findByUserIdAndDeletedAtIsNull(userId).map(UserMapper::toUserResponse);
    }

}
