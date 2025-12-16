package org.example.demo_ssr_v1.board;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BoardRepository extends JpaRepository<Board, Long> {

    // 자동 제공 메서드 (별도 구현 없이 사용 가능)
    // - save(T entity): (Insert 또는 Update)
    // - findById(ID id) : ID로 엔티티 조회 (Optional<T> 반환)
    // - findAll()
    // - deleteById(Id id): ID로 엔티티 삭제
    // - count(): 전체 개수 조회
    // - existsById(ID id): ID 존재 여부 확인

    // LAZY 로딩이라서 한번에 username을 가져와야 한다
    // JOIN FETCH

    // 게시글 전체 조회 (작성자 정보 포함 - JOIN FETCH 사용)
    @Query("SELECT b FROM Board b JOIN FETCH b.user ORDER BY b.createdAt DESC ")
    List<Board> findAllWithUserOrderByCreatedAtDesc();

    // 게시글 ID로 조회 (작성자 정보 포함 - JOIN FETCH 사용해야 함)
    @Query("SELECT b FROM Board b JOIN FETCH b.user WHERE  b.id = :id")
    Optional<Board> findByIdWithUser(@Param("id") Long id);
}
