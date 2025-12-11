package org.example.demo_ssr_v1.user;

import lombok.Data;

public class UserRequest {

    @Data
    public static class LoginDTO {
        private String username;
        private String password;

        public void validate() {
            if (username == null || username.trim().isEmpty())
                throw new IllegalArgumentException("사용자 명을 입력해주세요");

            if (password == null || password.trim().isEmpty())
                throw new IllegalArgumentException("비밀번호를 입력해주세요");
        }

    } // end of inner class

    @Data
    public static class JoinDTO {
        private String username;
        private String password;
        private String email;

        public void validate() {
            if (username == null || username.trim().isEmpty())
                throw new IllegalArgumentException("사용자 명을 입력해주세요");

            if (password == null || password.trim().isEmpty())
                throw new IllegalArgumentException("비밀번호를 입력해주세요");

            if (email == null || email.trim().isEmpty())
                throw new IllegalArgumentException("이메일을 입력해주세요");

            if (email.contains("@") == false)
                throw new IllegalArgumentException("올바른 이메일 형식이 아닙니다.");
        }

        // JoinDTO를 USER 타입으로 변환시키는 기능
        public User toEntity() {
            return User.builder()
                    .username(this.username)
                    .password(this.password)
                    .email(this.email)
                    .build();
        }

    } // end of inner class

    @Data
    public static class UpdateDTO {
        private String password;

        // username은 제외: 변경 불가능한 고유 식별자

        public void validate() {
            if (password == null || password.trim().isEmpty())
                throw new IllegalArgumentException("비밀번호를 입력해주세요");

            if (password.length() < 4)
                throw new IllegalArgumentException("비밀번호는 4글자 이상이어야 합니다.");
        }

    } // end of inner class


}
