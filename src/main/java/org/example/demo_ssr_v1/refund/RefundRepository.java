package org.example.demo_ssr_v1.refund;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RefundRepository extends JpaRepository<Refund, Long> {

    // 사용자 ID로 환불 요청 목록 조회 (최신순)
    // N + 1 방지를 위해서 한번에 JOIN FETCH를 사용해서 User를 가져올 예정
    @Query("""
    SELECT r FROM Refund r
    JOIN FETCH r.payment p
    JOIN FETCH p.user u
    WHERE r.user.id = :userId
    ORDER BY r.createdAt DESC
""")
    List<Refund> findAllByUserId(@Param("userId") Long userId);

    // 결제 ID로 환불 요청 조회 여부 확인
    Optional<Refund> findByPaymentId(Long paymentId);
}
