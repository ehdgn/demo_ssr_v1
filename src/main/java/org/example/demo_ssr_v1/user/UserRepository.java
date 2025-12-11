package org.example.demo_ssr_v1.user;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Repository
public class UserRepository {

    private final EntityManager em;

    // 회원 정보 수정 - 더티 체킹
    @Transactional
    public User updateById(Long id, UserRequest.UpdateDTO reqDTO) {
        // 1. 수정하려는 엔티티를 조회
        User user = findById(id);
        user.update(reqDTO); // User 상태값 변경

        return user;
    }

    // 회원정보 조회
    public User findById(Long id) {
        User user = em.find(User.class, id);

        if (user == null) {
            throw new RuntimeException("사용자를 찾을 수 없습니다.");
        }

        return user;
    }

    // 로그인용 사용자 조회: 사용자명과 비밀번호가 일치하는지 조회
    public User findByUsernameAndPassword(String username, String password) {
        try {
            String jpql = "SELECT u FROM User u WHERE u.username = :username AND u.password = :password";

            Query query = em.createQuery(jpql, User.class);
            query.setParameter("username", username);
            query.setParameter("password", password);
            return (User)query.getSingleResult();

        } catch (Exception e) {
            // 로그인 실패 의미 (이름이 없거나, 비밀번호가 불일치)
            return null;
        }
    }

    // 회원 가입
    @Transactional
    public User save(User user) {
        em.persist(user);
        return user;
    }

    // 사용자명 중복 체크 (조회)
    public User findByUsername(String username) {
        try {
            String jpql = "SELECT u FROM User u WHERE u.username = :username";
            return em.createQuery(jpql, User.class)
                    .setParameter("username", username)
                    .getSingleResult();
        } catch (Exception e) {
            // 사용자를 찾을 수 없을 경우 null 반환
            return null;
        }
    }
}
