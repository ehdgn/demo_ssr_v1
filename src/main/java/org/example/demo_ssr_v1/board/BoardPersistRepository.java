package org.example.demo_ssr_v1.board;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

// DB -- CRUD
@RequiredArgsConstructor
@Repository // IoC
public class BoardPersistRepository {

    // DI
    private final EntityManager entityManager;

    @Transactional
    public Board save(Board board) {
        // 엔티티 매니저 자동으로 insert 쿼리 만들어 던진다.
        entityManager.persist(board);
        return board;
    }

    // 게시글 전체 조회
    public List<Board> findAll() {
        return entityManager
                .createQuery("SELECT b FROM Board b ORDER BY b.createdAt DESC")
                .getResultList();
    }
}