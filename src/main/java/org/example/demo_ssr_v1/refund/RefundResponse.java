package org.example.demo_ssr_v1.refund;

import lombok.Data;

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
}
