package org.example.demo_ssr_v1.reply;

import lombok.Data;
import org.example.demo_ssr_v1._core.errors.exception.Exception400;
import org.example.demo_ssr_v1.board.Board;
import org.example.demo_ssr_v1.user.User;

public class ReplyRequest {

    // 댓글 작성
    @Data
    public static class SaveDTO {
        private Long boardId;
        private String comment;

        // 유효성 검사 (형식 검사)
        public void validate() {
            if (comment == null || comment.trim().isEmpty())
                throw new Exception400("댓글 내용을 입력해주세요");

            if (comment.length() > 500)
                throw new Exception400("댓글은 500자 이하여야 합니다.");

            if (boardId == null)
                throw new Exception400("게시글 ID가 필요합니다.");
        }

        // 서비스 단에서 DTO를 엔티티로 변환하는 편의 메서드 제공
        public Reply toEntity(Board board, User user) {
            return Reply.builder()
                    .comment(this.comment)
                    .board(board)
                    .user(user)
                    .build();
        }
    }

    @Data
    public static class UpdateDTO {
        private String comment;

        public void validate() {
            if (comment == null || comment.trim().isEmpty()) {
                throw new Exception400("댓글 내용을 입력해주세요.");
            }
            if (comment.length() > 500) {
                throw new Exception400("댓글은 500자 이하여야 합니다.");
            }
        }
    }


}
