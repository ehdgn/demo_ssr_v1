package org.example.demo_ssr_v1.user;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(
        name = "user_role_tb",
        // 같은 권한을 두번 가질 수 없게 제약 설정 (관리자, 일반사용자, 관리자(x))
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_user_role", columnNames = {"user_id", "role"})
        }
)
@NoArgsConstructor
@Getter
@Entity
public class UserRole {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role;

    @Builder
    public UserRole(Long id, Role role) {
        this.id = id;
        this.role = role;
    }
}
