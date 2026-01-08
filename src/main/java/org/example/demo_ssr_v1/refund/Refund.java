package org.example.demo_ssr_v1.refund;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.demo_ssr_v1.payment.Payment;
import org.example.demo_ssr_v1.user.User;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
@Table(name = "refund_tb")
@Entity
public class Refund {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 환불에 대한 요청자
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // 결제내역
    // 환불 정책 -- 전체 환불 정책 (1 : 1)
    // 환불 정책 -- 부분 환불 가능 정책 (하나의 결제내역 --> 여러개의 환불 내역 발생)
    // 원래 우리 프로젝트 비즈니스 로직에서는 전체 환불 정책 1:1 구조
    // 추후 확장성을 위해서 부분환불을 도입한다면 1:N 설계 되어야 한다.
    // @OneToOne 대신 @ManyToOne에 unique 제약 조건을 걸어 1:1로 구현
    // 추후 변경이 일어난다면, unique = true 만 제거하면 됨
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id", nullable = false, unique = true)
    private Payment payment;

    // 환불 사유
    @Column(length = 500)
    private String reason;

    // 환불 상태 (대기, 승인, 거절)
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private RefundStatus status = RefundStatus.PENDING; // 기본값 대기

    // 관리자 환불 거절 사유
    @Column(length = 500)
    private String rejectReason;

    // 생성시간
    @CreationTimestamp
    private Timestamp createdAt;

    // 수정시간
    @CreationTimestamp
    private Timestamp updatedAt;

    // 사용자가 먼저 환불 요청에 의해서 -> row 생성 되기 때문 (reason <- 사용자 환불 사유)
    @Builder
    public Refund (User user, Payment payment, String reason) {
        this.user = user;
        this.payment = payment;
        this.reason = reason;
        this.status = RefundStatus.PENDING;
    }

    // 편의 기능

    // 환불 승인 처리
    public void approve() {
        this.status = RefundStatus.APPROVED;
    }

    public void reject(String rejectReason) {
        this.status = RefundStatus.REJECTED;
        this.rejectReason = rejectReason;
    }

    // 대기중인 상태인지 확인
    public boolean isPending() {
        return this.status == RefundStatus.PENDING;
    }

    public boolean isApproved() {
        return this.status == RefundStatus.APPROVED;
    }

    public boolean isRejected() {
        return this.status == RefundStatus.REJECTED;
    }


}