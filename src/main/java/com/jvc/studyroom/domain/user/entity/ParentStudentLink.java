package com.jvc.studyroom.domain.user.entity;

import com.jvc.studyroom.common.enums.LinkStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "parent_student_links",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"parent_id", "student_id"})
    }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ParentStudentLink {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id", nullable = false)
    private User parent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private LinkStatus status = LinkStatus.PENDING;

    @Column(length = 6)
    private String verificationCode;

    private LocalDateTime verificationExpiredAt;

    @Column(nullable = false)
    private boolean canManageReservation = true;  // 부모가 학생 예약 관리 가능 여부

    @Column(nullable = false)
    private boolean canViewStatistics = true;     // 부모가 학생 통계 조회 가능 여부

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime confirmedAt;

    // 비즈니스 메서드
    public void confirm() {
        this.status = LinkStatus.CONFIRMED;
        this.confirmedAt = LocalDateTime.now();
        this.verificationCode = null;
        this.verificationExpiredAt = null;
    }

    public void reject() {
        this.status = LinkStatus.REJECTED;
    }

    public void updatePermissions(boolean canManageReservation, boolean canViewStatistics) {
        this.canManageReservation = canManageReservation;
        this.canViewStatistics = canViewStatistics;
    }

    public boolean isVerificationExpired() {
        return this.verificationExpiredAt != null &&
               this.verificationExpiredAt.isBefore(LocalDateTime.now());
    }
}