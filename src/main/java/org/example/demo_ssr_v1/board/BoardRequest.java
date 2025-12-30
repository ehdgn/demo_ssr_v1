package org.example.demo_ssr_v1.board;

import lombok.Data;
import org.example.demo_ssr_v1.user.User;

// 정적 내부 클래스 활용
// 컨트롤러와 비즈니스 로직 사이에서 데이터를 전송하는 객체
public class BoardRequest {

    // 게시글 저장 DTO
    @Data
    public static class SaveDTO {
        private String title;
        private String content;
        private String username;
        private Boolean premium;

        public Board toEntity(User user) {
            return Board.builder()
                    .title(title)
                    .content(content)
                    .user(user)
                    // 체크 박스 주의
                    .premium(premium != null ? premium : false)
                    .build();
        }
    }

    @Data
    public static class UpdateDTO {
        private String title;
        private String content;
        private String username;
        private Boolean premium;

        // 검증 메서드
        public void validate() {
            if (title == null || title.trim().isEmpty()) {
                throw new IllegalArgumentException("제목은 필수 입니다.");
            }

            if (content == null || content.trim().isEmpty()) {
                throw new IllegalArgumentException("내용은 필수입니다.");
            }
        }
    }

}
