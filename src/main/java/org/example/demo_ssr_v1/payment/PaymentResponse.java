package org.example.demo_ssr_v1.payment;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

public class PaymentResponse {

    @Data
    public static class PrepareDTO {
        private String merchantUid; // 생성된 우리 서버 주문 번호
        private Integer amount;     // 결제 금액
        private String impKey;      // 포트원 REST API 키 (필수)

        public PrepareDTO(String merchantUid, Integer amount, String impKey) {
            this.merchantUid = merchantUid;
            this.amount = amount;
            this.impKey = impKey;
        }
    }

    // 결제 검증 응답 DTO - JS로 내려줄 데이터
    @Data
    public static class VerifyDTO {
        private Integer amount;
        private Integer currentPoint;

        public VerifyDTO(Integer amount, Integer currentPoint) {
            this.amount = amount;
            this.currentPoint = currentPoint;
        }
    }

    // 포트원 엑세스 토큰 응답 DTO 설계
    @Data
    public static class PortOneTokenResponse {
        private int code;
        private String message;
        // 중첩 객체를 설계 해야 함
        private ResponseData response;

        @Data
        @JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
        public static class ResponseData {
            // access_token --> @JsonNaming --> accessToken
            private String accessToken;
            private int now;
            private int expiredAt;
        }
    }

    // 포트원 결제(포트원 서버에 DB 저장되어 있음) 조회 응답 DTO
    @Data
    public static class PortOnePaymentResponse {
        private int code;
        private String message;
        private PaymentData response;

        @Data
        @JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
        public static class PaymentData {
            private Integer amount;
            private String impUid;
            private String merchantUid;
            private String status;
            private Long paidAt;
        }
    }
}
