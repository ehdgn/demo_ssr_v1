package org.example.demo_ssr_v1.purchase;

import lombok.Data;
import org.example.demo_ssr_v1._core.utils.MyDateUtil;

public class PurchaseResponse {

    // 유료 게시글 구매 목록 (세션 사용자 기준)
    @Data
    public static class ListDTO {
        private Long id;
        private Long boardId;
        private String boardTitle;
        private String boardAuthor;
        private Integer price;
        private String purchasedAt;

        public ListDTO(Purchase purchase) {
            this.id = purchase.getId();
            this.price = purchase.getPrice();

            // 내가 구매한 일시 포맷팅
            if (purchase.getCreatedAt() != null) {
                this.purchasedAt = MyDateUtil.timestampFormat(purchase.getCreatedAt());
            }

            // 평탄화
            // JOIN FETCH 를 통해서 한번에 들고 오는 상태이다.
            if (purchase.getBoard() != null) {
                this.boardId = purchase.getBoard().getId();
                this.boardTitle = purchase.getBoard().getTitle();

                if (purchase.getBoard().getUser() != null) {
                    this.boardAuthor = purchase.getBoard().getUser().getUsername();
                }
            }
        }
    }
}
