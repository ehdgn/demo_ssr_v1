package org.example.demo_ssr_v1.user;

import lombok.Data;

/**
 * 사용자 응답 DTO
 */

public class UserResponse {

    /**
     * 회원 정보 수정 화면 DTO
     */
    @Data
    public static class UpdateFormDTO {
        private Long id;
        private String username;
        private String email;

        public UpdateFormDTO(User user) {
            this.id = user.getId();
            this.username = user.getUsername();
            this.email = user.getEmail();
        }
    }

    /**
     * 로그인 응답 DTO (세션 저장용)
     * - 세션에 엔티티 정보를 저장하지만
     * 다른 곳으로 전달 할 떄는 DTO를 사용하는 것이 권장 사항이다.
     */
    @Data
    public static class LoginDTO {
        private Long id;
        private String username;
        private String email;

        public LoginDTO(User user) {
            this.id = user.getId();
            this.username = user.getUsername();
            this.email = user.getEmail();
        }
    }

}
