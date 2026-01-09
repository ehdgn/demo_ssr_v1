package org.example.demo_ssr_v1.refund;

import lombok.Data;
import org.example.demo_ssr_v1._core.utils.MyDateUtil;

public class RefundResponse {

    @Data
    public static class ListDTO {
        private Long id;
        private Long paymentId;
        private Integer amount;
        private String reason; // 환불 사유 (사용자)
        private String rejectReason; // 거절 사유 (관리자)
        private String statusDisplay; // 화면 표시용 (대기중, 승인, 거절)

        // 상태별 플래그 변수 사용 (화면 표시용)
        private boolean isPending; // 대기중
        private boolean isApproved; // 승인
        private boolean isRejected; // 거절

        public ListDTO(Refund refund) {
            this.id = refund.getId();
            this.paymentId = refund.getPayment().getId();
            this.amount = refund.getPayment().getAmount();
            this.reason = refund.getReason();
            this.rejectReason = refund.getRejectReason() == null ? "" : refund.getRejectReason();

            // 스위치 표현식 (14버전 이후 부터 사용 가능)
            switch (refund.getStatus()) {
                case PENDING -> this.statusDisplay = "대기중";
                case APPROVED -> this.statusDisplay = "승인됨";
                case REJECTED -> this.statusDisplay = "거절됨";
            }

            this.isPending = (refund.getStatus() == RefundStatus.PENDING);
            this.isApproved = (refund.getStatus() == RefundStatus.APPROVED);
            this.isRejected = (refund.getStatus() == RefundStatus.REJECTED);
        }
    }

    @Data
    public static class AdminListDTO {
        private Long id;
        private String username;
        private Long paymentId; // 결제 PK
        private String impUid; // 포트원으로 승인 요청 할 때
        private String merchantUid; // 주문 번호 (가맹점)
        private Integer amount;
        private String requestAt; // 환불 요청 일시
        private RefundStatus status; // 환불 상태
        private String statusDisplay; // 머스태치용 표시
        private String reason; // 환불 사유 (사용자)
        private String rejectReason; // 거절 사유 (관리자)

        public AdminListDTO(Refund refund) {
            // JOIN FETCH 로 한방에 User와 Payment 가지고 옴
            this.id = refund.getId();
            this.username = refund.getUser().getUsername();
            this.paymentId = refund.getPayment().getId();
            this.impUid = refund.getPayment().getImpUid();
            this.merchantUid = refund.getPayment().getMerchantUid();
            this.amount = refund.getPayment().getAmount();
            this.status = refund.getStatus();

            this.reason = refund.getReason();
            this.rejectReason = refund.getRejectReason();

            // 변환 -> 대기중/승인됨/거절됨
            switch (refund.getStatus()) {
                case PENDING -> this.statusDisplay = "대기중";
                case APPROVED -> this.statusDisplay = "승인됨";
                case REJECTED -> this.statusDisplay = "거절됨";
            }

            // 날짜 포맷
            if (refund.getCreatedAt() != null) {
                this.requestAt = MyDateUtil.timestampFormat(refund.getCreatedAt());
            }
        }
    }
}