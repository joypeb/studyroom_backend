package com.jvc.studyroom.domain.user.service;

import com.jvc.studyroom.common.enums.UserRole;
import com.jvc.studyroom.common.utils.PageableUtil;
import com.jvc.studyroom.domain.user.converter.UserMapper;
import com.jvc.studyroom.domain.user.dto.UserResponse;
import com.jvc.studyroom.domain.user.repository.UserRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class DefaultUserService implements UserService {

    private final UserRepository userRepository;
    private final PageableUtil pageableUtil;

    @Override
    public Mono<Page<UserResponse>> findAllUsers(Pageable pageable) {
        Flux<UserResponse> user = userRepository.findAllByDeletedAtIsNull(pageable).map(UserMapper::toUserResponse);
        Mono<Long> count = userRepository.countByDeletedAtIsNull();

        return pageableUtil.createPageResponse(user, count, pageable);
    }

    @Override
    public Mono<UserResponse> findUserById(UUID userId) {
        return userRepository.findByUserIdAndDeletedAtIsNull(userId).map(UserMapper::toUserResponse);
    }

    @Override
    public Mono<Page<UserResponse>> findAllUsersByRole(String role, Pageable pageable) {
        Flux<UserResponse> user = userRepository.findAllByRoleAndDeletedAtIsNull(UserRole.fromString(role), pageable).map(UserMapper::toUserResponse);
        Mono<Long> count = userRepository.countByRoleAndDeletedAtIsNull(UserRole.fromString(role));

        return pageableUtil.createPageResponse(user, count, pageable);
    }

}
