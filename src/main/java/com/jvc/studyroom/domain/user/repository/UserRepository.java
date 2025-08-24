package com.jvc.studyroom.domain.user.repository;

import com.jvc.studyroom.common.enums.AccountStatus;
import com.jvc.studyroom.common.enums.UserRole;
import com.jvc.studyroom.domain.user.model.User;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.query.Param;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserRepository extends R2dbcRepository<User, UUID> {
    Flux<User> findAllByDeletedAtIsNull();
    Mono<User> findByUserIdAndDeletedAtIsNull(UUID userId);
    Mono<Long> countByEmailAndDeletedAtIsNull(String email);
    Mono<User> findByEmailAndDeletedAtIsNull(String email);
    Flux<User> findAllByAccountStatus(Pageable pageable, AccountStatus accountStatus);
    Mono<User> findByUserId(UUID userId);
    Flux<User> findAllByRoleAndAccountStatus(UserRole role, AccountStatus accountStatus);
    Flux<User> findAllByRoleAndAccountStatus(UserRole role, Pageable pageable, AccountStatus accountStatus);
    Mono<Long> countByRoleAndAccountStatus(UserRole role, AccountStatus accountStatus);
    Mono<Long> countByAccountStatus(AccountStatus accountStatus);
    @Query("UPDATE users SET account_status = :accountStatus, updated_at = NOW() WHERE user_id = :userId")
    Mono<Integer> updateAccountStatus(@Param("userId") UUID userId, @Param("accountStatus") AccountStatus accountStatus);
    @Query("UPDATE users SET assigned_seat_id = :seatId, updated_at = NOW() WHERE user_id = :userId")
    Mono<Integer> updateAssignedSeatId(@Param("userId") UUID userId, @Param("seatId") UUID seatId);
    Mono<User> findUserByAssignedSeatId(UUID seatId);
    Flux<User> findAllByAccountStatusNot(Pageable pageable, AccountStatus accountStatus);
    Mono<Long> countByAccountStatusNot(AccountStatus accountStatus);
}