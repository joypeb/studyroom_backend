package com.jvc.studyroom.domain.branch.entity;

import com.jvc.studyroom.common.enums.BranchStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "branches")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Branch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Column(nullable = false, unique = true, length = 50)
    private String code;  // 지점 코드

    @Column(nullable = false, length = 200)
    private String address;

    @Column(length = 100)
    private String detailAddress;

    @Column(length = 10)
    private String zipCode;

    @Column(nullable = false, length = 20)
    private String contactNumber;

    @Column(length = 100)
    private String businessNumber;  // 사업자번호

    // 운영 시간
    @Column(nullable = false)
    private LocalTime openTime;

    @Column(nullable = false)
    private LocalTime closeTime;

    // 24시간 운영 여부
    @Column(nullable = false)
    private boolean is24Hours = false;

    // 휴무일 정보 (JSON 형태로 저장)
    @Column(columnDefinition = "TEXT")
    private String holidays;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private BranchStatus status = BranchStatus.ACTIVE;

    // 좌석 수
    @Column(nullable = false)
    private int totalSeats = 0;

    // 설정
    @Column(nullable = false)
    private boolean autoConfirmReservation = false;  // 예약 자동 승인

    @Column(nullable = false)
    private int maxReservationDays = 7;  // 최대 예약 가능일

    @Column(nullable = false)
    private int maxReservationHours = 12;  // 최대 예약 시간

    @Column(nullable = false)
    private int minReservationHours = 1;   // 최소 예약 시간

    @Column(nullable = false)
    private int reservationUnitMinutes = 30;  // 예약 단위 (분)

    @Column(nullable = false)
    private int checkInTimeMinutes = 10;  // 체크인 제한 시간 (분)

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    // 비즈니스 메서드
    public void updateStatus(BranchStatus status) {
        this.status = status;
    }

    public void updateOperatingHours(LocalTime openTime, LocalTime closeTime, boolean is24Hours) {
        this.openTime = openTime;
        this.closeTime = closeTime;
        this.is24Hours = is24Hours;
    }

    public void updateReservationSettings(int maxDays, int maxHours, int minHours, int unitMinutes) {
        this.maxReservationDays = maxDays;
        this.maxReservationHours = maxHours;
        this.minReservationHours = minHours;
        this.reservationUnitMinutes = unitMinutes;
    }

    public boolean isOperating() {
        return this.status == BranchStatus.ACTIVE;
    }
}

