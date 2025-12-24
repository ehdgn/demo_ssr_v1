package org.example.demo_ssr_v1.user;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
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

    // 카카오 JWT(엑세스 토큰) DTO 설계
    @JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
    // @JsonNaming: LowerCamelCaseStrategy  -> token_type -> 이걸 자동으로 camelCase로 변경해줌
    // @JsonNaming: SnakeCaseStrategy -> tokenType -> 이걸 자동으로 snake_case로 변경해줌
    @Data
    public static class OAuthToken {
        private String tokenType;
        private String accessToken;
        private String expiresIn;
        private String refreshToken;
        private String refreshTokenExpiresIn;
    }

    @Data
    @JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class KakaoProfile {
        private Long id;
        private String connectedAt;
        private Properties properties;
    }

    @Data
    @JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class Properties {
        private String nickname;
        private String profile_image;
        private String thumbnail_image;
    }
}