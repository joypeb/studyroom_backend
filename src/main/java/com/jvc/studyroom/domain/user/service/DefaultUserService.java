package com.jvc.studyroom.domain.user.service;

import com.jvc.studyroom.common.dto.PaginationRequest;
import com.jvc.studyroom.common.enums.AccountStatus;
import com.jvc.studyroom.common.enums.UserRole;
import com.jvc.studyroom.common.utils.PageableUtil;
import com.jvc.studyroom.domain.relation.entity.StudentParentRelation;
import com.jvc.studyroom.domain.relation.repository.RelationRepository;
import com.jvc.studyroom.domain.user.converter.UserMapper;
import com.jvc.studyroom.domain.user.dto.UserResponse;
import com.jvc.studyroom.domain.user.dto.UserRoleRequest;
import com.jvc.studyroom.domain.user.dto.UserStatusRequest;
import com.jvc.studyroom.domain.user.dto.UserUpdateRequest;
import com.jvc.studyroom.domain.user.model.User;
import com.jvc.studyroom.domain.user.repository.UserRepository;
import com.jvc.studyroom.exception.ErrorCode;
import com.jvc.studyroom.exception.customExceptions.StudyroomServiceException;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class DefaultUserService implements UserService {

    private final UserRepository userRepository;
    private final RelationRepository relationRepository;
    private final PageableUtil pageableUtil;

    private final static AccountStatus STATUS_ACTIVE = AccountStatus.ACTIVE;
    private final static Set<UserRole> allowedRoles = Set.of(UserRole.ADMIN, UserRole.SUPER_ADMIN);

    @Override
    public Mono<Page<UserResponse>> findAllUsers(PaginationRequest request, User user) {
        if (!(allowedRoles.contains(user.getRole()))) {
            throw new StudyroomServiceException(ErrorCode.ACCESS_DENIED);
        }

        Pageable pageable = createPageable(request.getPage(), request.getSize(), request.getSortBy(), request.getSortDirection());

        Flux<UserResponse> userFlux = userRepository.findAllByAccountStatusNot(pageable, AccountStatus.DELETED).map(UserMapper::toUserResponse);
        Mono<Long> count = userRepository.countByAccountStatusNot(AccountStatus.DELETED);

        return pageableUtil.createPageResponse(userFlux, count, pageable);
    }

    @Override
    public Mono<UserResponse> findUserById(UUID userId, User user) {
        return Mono.just(user)
                .filter(u -> u.getUserId().equals(userId) || allowedRoles.contains(u.getRole()))
                .switchIfEmpty(Mono.error(new StudyroomServiceException(ErrorCode.ACCESS_DENIED)))
                .flatMap(u -> userRepository.findByUserId(userId))
                .switchIfEmpty(Mono.error(new StudyroomServiceException(ErrorCode.USER_NOT_FOUND)))
                .map(UserMapper::toUserResponse);
    }

    @Override
    public Mono<Page<UserResponse>> findAllUsersByRole(UserRoleRequest request, User user) {
        if (!(allowedRoles.contains(user.getRole()))) {
            throw new StudyroomServiceException(ErrorCode.ACCESS_DENIED);
        }

        Pageable pageable = createPageable(request.getPage(), request.getSize(), request.getSortBy(), request.getSortDirection());

        Flux<UserResponse> userFlux = userRepository.findAllByRoleAndAccountStatus(UserRole.fromString(request.getRole().toUpperCase()), pageable,
                        STATUS_ACTIVE)
                .map(UserMapper::toUserResponse);
        Mono<Long> count = userRepository.countByRoleAndAccountStatus(UserRole.fromString(request.getRole()), STATUS_ACTIVE);

        return pageableUtil.createPageResponse(userFlux, count, pageable);
    }

    @Override
    public Mono<Integer> updateUserStatusById(UUID userId, UserStatusRequest request, User user) {
        return Mono.just(user)
                .filter(u -> allowedRoles.contains(u.getRole()))
                .switchIfEmpty(Mono.error(new StudyroomServiceException(ErrorCode.ACCESS_DENIED)))
                .flatMap(u -> userRepository.updateAccountStatus(userId, request.getAccountStatus()));
    }

    @Override
    public Mono<UserResponse> updateUser(UUID userId, UserUpdateRequest request, User user) {
        return Mono.just(user)
                .filter(u -> u.getUserId().equals(userId) || allowedRoles.contains(u.getRole()))
                .switchIfEmpty(Mono.error(new StudyroomServiceException(ErrorCode.ACCESS_DENIED)))
                .flatMap(u -> userRepository.findByUserId(userId))
                .switchIfEmpty(Mono.error(new StudyroomServiceException(ErrorCode.USER_NOT_FOUND)))
                .filter(targetUser -> targetUser.getAccountStatus().equals(AccountStatus.ACTIVE))
                .switchIfEmpty(Mono.error(new StudyroomServiceException(ErrorCode.USER_ACCOUNT_DEACTIVATED)))
                .flatMap(existingUser -> userRepository.save(UserMapper.toUpdateUser(existingUser, request))
                        .map(UserMapper::toUserResponse));
    }

    @Override
    public Flux<UserResponse> findStudentsByParentId(UUID parentId, User user) {

        return Mono.just(user)
                // 권한 검증: 본인(부모)이거나 관리자
                .filter(u -> (u.getUserId().equals(parentId) && u.getRole() == UserRole.PARENTS) ||
                        allowedRoles.contains(u.getRole()))
                .switchIfEmpty(Mono.error(new StudyroomServiceException(ErrorCode.ACCESS_DENIED)))
                // 대상 부모 존재 확인
                .flatMap(u -> userRepository.findByUserId(parentId))
                .switchIfEmpty(Mono.error(new StudyroomServiceException(ErrorCode.USER_NOT_FOUND)))
                // 대상이 부모 역할인지 확인
                .filter(targetUser -> targetUser.getRole() == UserRole.PARENTS)
                .switchIfEmpty(Mono.error(new StudyroomServiceException(ErrorCode.INVALID_USER_ROLE)))
                // 계정 상태 확인
                .filter(targetUser -> targetUser.getAccountStatus() == AccountStatus.ACTIVE)
                .switchIfEmpty(Mono.error(new StudyroomServiceException(ErrorCode.USER_ACCOUNT_DEACTIVATED)))
                // 부모-학생 관계 조회
                .flatMapMany(parentUser -> relationRepository.findAllByParentId(parentId))
                .filter(StudentParentRelation::getIsActive)
                .map(StudentParentRelation::getStudentId)
                .distinct()
                // 각 학생 정보 조회
                .flatMap(studentId -> userRepository.findByUserId(studentId)
                        .filter(student -> student.getAccountStatus() == AccountStatus.ACTIVE)
                        .onErrorResume(error -> {
                            // 특정 학생 조회 실패 시 로그만 남기고 스킵
                            log.warn("Failed to find student with ID: {}, error: {}", studentId, error.getMessage());
                            return Mono.empty();
                        }))
                .map(UserMapper::toUserResponse);
    }

    @Override
    public Flux<UserResponse> findParentsByStudentId(UUID studentId, User user) {

        return Mono.just(user)
                // 권한 검증: 본인(학생)이거나 관리자
                .filter(u -> (u.getUserId().equals(studentId) && u.getRole() == UserRole.STUDENT) ||
                        allowedRoles.contains(u.getRole()))
                .switchIfEmpty(Mono.error(new StudyroomServiceException(ErrorCode.ACCESS_DENIED)))
                // 대상 학생 존재 확인
                .flatMap(u -> userRepository.findByUserId(studentId))
                .switchIfEmpty(Mono.error(new StudyroomServiceException(ErrorCode.USER_NOT_FOUND)))
                // 대상이 학생 역할인지 확인
                .filter(targetUser -> targetUser.getRole() == UserRole.STUDENT)
                .switchIfEmpty(Mono.error(new StudyroomServiceException(ErrorCode.INVALID_USER_ROLE)))
                // 계정 상태 확인
                .filter(targetUser -> targetUser.getAccountStatus() == AccountStatus.ACTIVE)
                .switchIfEmpty(Mono.error(new StudyroomServiceException(ErrorCode.USER_ACCOUNT_DEACTIVATED)))
                // 학생-부모 관계 조회
                .flatMapMany(studentUser -> relationRepository.findAllByStudentId(studentId))
                .filter(StudentParentRelation::getIsActive)
                .map(StudentParentRelation::getParentId)
                .distinct()
                // 각 부모 정보 조회
                .flatMap(parentId -> userRepository.findByUserId(parentId)
                        .filter(parent -> parent.getAccountStatus() == AccountStatus.ACTIVE)
                        .onErrorResume(error -> {
                            // 특정 부모 조회 실패 시 로그만 남기고 스킵
                            log.warn("Failed to find parent with ID: {}, error: {}", parentId, error.getMessage());
                            return Mono.empty();
                        }))
                .map(UserMapper::toUserResponse);
    }

    private Pageable createPageable(Integer page, Integer size, String sortBy, String sortDirection) {
        return pageableUtil.createPageable(page, size, sortBy, sortDirection);
    }
}
