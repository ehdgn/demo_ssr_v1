package org.example.demo_ssr_v1.refund;

import lombok.Data;
import org.example.demo_ssr_v1._core.errors.exception.Exception400;

public class RefundRequest {

    @Data
    public static class RequestDTO {
        private Long paymentId;
        private String reason;

        public void validate() {
            if (paymentId == null) {
                throw new Exception400("결제 아이디는 필수입니다.");
            }
            if (reason == null || reason.trim().isEmpty()) {
                throw new Exception400("환불 사유는 필수입니다.");
            }
            if (reason.length() > 500) {
                throw new Exception400("환불 사유는 500자 이하여야 합니다.");
            }
        }
    }
}
