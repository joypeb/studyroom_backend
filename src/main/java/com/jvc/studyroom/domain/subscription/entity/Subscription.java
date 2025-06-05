package com.jvc.studyroom.domain.subscription.entity;

import com.jvc.studyroom.common.enums.PaymentMethod;
import com.jvc.studyroom.common.enums.SubscriptionStatus;
import com.jvc.studyroom.common.enums.SubscriptionType;
import com.jvc.studyroom.domain.branch.entity.Branch;
import com.jvc.studyroom.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "subscriptions", indexes = {
    @Index(name = "idx_user_status", columnList = "user_id, status"),
    @Index(name = "idx_expired_at", columnList = "expiredAt")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Subscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "branch_id", nullable = false)
    private Branch branch;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private SubscriptionType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SubscriptionStatus status = SubscriptionStatus.PENDING;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(nullable = false)
    private LocalDateTime startedAt;

    @Column(nullable = false)
    private LocalDateTime expiredAt;

    // 이용 가능 시간 (시간제 이용권용)
    private Integer availableHours;

    private Integer usedHours = 0;

    // 결제 정보
    @Column(length = 100)
    private String paymentId;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private PaymentMethod paymentMethod;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime canceledAt;

    @Column(length = 200)
    private String cancelReason;

    // 자동 갱신 여부
    @Column(nullable = false)
    private boolean autoRenew = false;

    // 비즈니스 메서드
    public void activate() {
        this.status = SubscriptionStatus.ACTIVE;
        this.startedAt = LocalDateTime.now();
    }

    public void expire() {
        this.status = SubscriptionStatus.EXPIRED;
    }

    public void cancel(String reason) {
        this.status = SubscriptionStatus.CANCELED;
        this.canceledAt = LocalDateTime.now();
        this.cancelReason = reason;
        this.autoRenew = false;
    }

    public void useHours(int hours) {
        if (this.availableHours != null) {
            this.usedHours += hours;
            if (this.usedHours >= this.availableHours) {
                this.status = SubscriptionStatus.EXHAUSTED;
            }
        }
    }

    public boolean isValid() {
        return this.status == SubscriptionStatus.ACTIVE &&
               this.expiredAt.isAfter(LocalDateTime.now()) &&
               (this.availableHours == null || this.usedHours < this.availableHours);
    }

    public int getRemainingHours() {
        if (this.availableHours == null) {
            return Integer.MAX_VALUE;  // 무제한
        }
        return Math.max(0, this.availableHours - this.usedHours);
    }
}

