package com.jvc.studyroom.domain.user.service;

import com.jvc.studyroom.common.dto.PaginationRequest;
import com.jvc.studyroom.common.enums.AccountStatus;
import com.jvc.studyroom.common.enums.UserRole;
import com.jvc.studyroom.common.utils.PageableUtil;
import com.jvc.studyroom.domain.user.converter.UserMapper;
import com.jvc.studyroom.domain.user.dto.UserResponse;
import com.jvc.studyroom.domain.user.dto.UserRoleRequest;
import com.jvc.studyroom.domain.user.dto.UserStatusRequest;
import com.jvc.studyroom.domain.user.dto.UserUpdateRequest;
import com.jvc.studyroom.domain.user.repository.UserRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class DefaultUserService implements UserService {

    private final UserRepository userRepository;
    private final PageableUtil pageableUtil;

    private final static AccountStatus STATUS_ACTIVE = AccountStatus.ACTIVE;

    @Override
    public Mono<Page<UserResponse>> findAllUsers(PaginationRequest request) {
        Pageable pageable = createPageable(request.getPage(), request.getSize(), request.getSortBy(), request.getSortDirection());

        Flux<UserResponse> user = userRepository.findAllByAccountStatusNot(pageable, AccountStatus.DELETED).map(UserMapper::toUserResponse);
        Mono<Long> count = userRepository.countByAccountStatusNot(AccountStatus.DELETED);

        return pageableUtil.createPageResponse(user, count, pageable);
    }

    @Override
    public Mono<UserResponse> findUserById(UUID userId) {
        return userRepository.findByUserId(userId).map(UserMapper::toUserResponse);
    }

    @Override
    public Mono<Page<UserResponse>> findAllUsersByRole(UserRoleRequest request) {
        Pageable pageable = createPageable(request.getPage(), request.getSize(), request.getSortBy(), request.getSortDirection());

        Flux<UserResponse> user = userRepository.findAllByRoleAndAccountStatus(UserRole.fromString(request.getRole().toUpperCase()), pageable, STATUS_ACTIVE)
                .map(UserMapper::toUserResponse);
        Mono<Long> count = userRepository.countByRoleAndAccountStatus(UserRole.fromString(request.getRole()), STATUS_ACTIVE);

        return pageableUtil.createPageResponse(user, count, pageable);
    }

    @Override
    public Mono<Integer> updateUserStatusById(UUID userId, UserStatusRequest request) {
        return userRepository.updateAccountStatus(userId, request.getAccountStatus());
    }

    @Override
    public Mono<UserResponse> updateUser(UUID userId, UserUpdateRequest request) {
        return userRepository.findByUserId(userId)
                .switchIfEmpty(Mono.error(new Exception("해당 사용자가 존재하지 않습니다")))
                .filter(user -> user.getAccountStatus().equals(AccountStatus.ACTIVE))
                .switchIfEmpty(Mono.error(new Exception("해당 사용자가 존재하지 않습니다")))
                .flatMap(existingUser -> userRepository.save(UserMapper.toUpdateUser(existingUser, request)).map(UserMapper::toUserResponse));
    }

    private Pageable createPageable(Integer page, Integer size, String sortBy, String sortDirection) {
        return pageableUtil.createPageable(page, size, sortBy, sortDirection);
    }
}
