package org.example.demo_ssr_v1.purchase;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PurchaseRepository extends JpaRepository<Purchase, Long> {
    //  Purchase p, Board b b.title, b.user.username
    @Query("""
        SELECT p FROM Purchase p
        JOIN FETCH p.board b
        JOIN FETCH b.user
        WHERE p.user.id = :userId
        ORDER BY p.createdAt DESC
    """)
    List<Purchase> findAllByUserIdWithBoard(@Param("userId") Long userId);

    // 사용자와 게시글의 구매 내역 조회
    @Query("SELECT p FROM Purchase p WHERE p.user.id = :userId AND p.board.id = :boardId")
    Optional<Purchase> findByUserIdAndBoardId(@Param("userId") Long userId,
                                              @Param("boardId") Long boardId);

    // 사용자와 게시글의 구매 여부 확인
    // default <- 자바 8+ 지원해주는 키워드
    default boolean existsByUserIdAndBoardId(Long userId, Long boardId) {
        return findByUserIdAndBoardId(userId, boardId).isPresent();
    }
}
