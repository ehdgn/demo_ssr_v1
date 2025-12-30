package org.example.demo_ssr_v1.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

// JpaRepository -> @Repository 있음 -> IoC 됨
public interface UserRepository extends JpaRepository<User, Long> {

    // 쿼리 메서드 네이밍 (자동 쿼리 생성)
    Optional<User> findByUsername(String username);

    // 이메일 존재 여부 확인 쿼리 네임드 메서드 설계
    Optional<User> findByEmail(String email);

    /**
     * 로그인 시 역할(ROLE) 정보까지 함께 조회되는 메서드
     * - 세션에 저장된 User 객체에서 isAdmin(), getRoleDisplay() 등을 바로 사용할 수 있다.
     */
    @Query("SELECT distinct u FROM User u LEFT JOIN FETCH u.roles r " +
            "WHERE u.username = :username ")
    Optional<User> findByUsernameAndWithRoles(@Param("username") String username);

    // JPQL (객체 쿼리)
    // ... ... ... Query DSL

    /**
     * JpaRepository에서 자동 제공되는 메서드들:
     *
     * 1. <S extends User> S save(S entity):
     *    - 엔티티 저장 (INSERT 또는 UPDATE)
     *    - ID가 null이면 INSERT, 있으면 UPDATE
     *
     * 2. Optional<User> findById(Long id):
     *    - ID로 엔티티 조회
     *    - Optional로 반환하여 null 안전성 보장
     *
     * 3. void deleteById(Long id):
     *    - ID로 엔티티 삭제
     *
     * 4. List<User> findAll():
     *    - 모든 엔티티 조회
     *
     * 더티 체킹 활용:
     * - 엔티티를 조회한 후 필드 값을 변경하면
     * - 트랜잭션이 끝날 때 자동으로 UPDATE 쿼리 실행
     * - 별도의 update 메서드가 필요 없음
     *
     * 예시:
     * User user = userRepository.findById(id).orElseThrow(...);
     * user.update(updateDTO); // 필드 값 변경
     * // 트랜잭션 종료 시 자동으로 UPDATE 쿼리 실행 (더티 체킹)
     */
}