package org.example.demo_ssr_v1.payment;

import lombok.RequiredArgsConstructor;
import org.example.demo_ssr_v1._core.errors.exception.Exception400;
import org.example.demo_ssr_v1._core.errors.exception.Exception404;
import org.example.demo_ssr_v1.user.User;
import org.example.demo_ssr_v1.user.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;

    @Value("${portone.imp-key}")
    private String impKey;

    @Value("${portone.imp-secret}")
    private String impSecret;

    // 1. 사전 결제 요청
    // 프론트엔드가 결제창을 띄우기 전에, 서버에서 먼저 고유한 주문번호(merchantUid)를 생성해서 내려주기 위함
    // - 중복결제 방지, 금액 위/변조 방지

    @Transactional
    public PaymentResponse.PrepareDTO 결제요청생성(Long userId, Integer amount) {
        if (!userRepository.existsById(userId)) {
            throw new Exception404("사용자를 찾을 수 없습니다.");
        }

        // 주문 번호 생성 (UUID 사용, 중복 시 재생성 로직 추가)
        String merchantUid = generateMerchantUid(userId);
        while (paymentRepository.existsByMerchantUid(merchantUid)) {
            merchantUid = generateMerchantUid(userId);
        }

        return new PaymentResponse.PrepareDTO(merchantUid, amount, impKey);
    }

    // 주문번호 생성 유틸리티
    // 형식: point_{userId}_{timestamp}_{uuid}
    private String generateMerchantUid(Long userId) {
        return "point_" + userId + "_" + System.currentTimeMillis() + "_" + UUID.randomUUID().toString().substring(0, 8);
    }

    @Transactional
    public PaymentResponse.VerifyDTO 결제검증및충전(Long userId, String impUid, String merchantUid) {
        // 실제 UserId로 사용자 존재 확인
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new Exception404("사용자를 찾을 수 없습니다."));

        // 중복 지급 방지
        if (paymentRepository.findByImpUid(impUid).isPresent()) {
            throw new Exception400("이미 처리된 결제 입니다.");
        }

        // 위변조 방지 때문에 검증 (500결제 --> 500만원 포인트 충전을 박아야 한다)
        // 외부 통신 시작 (인증서버 --> JWT --> 포트원 자원 서버에 조회 요청)
        PaymentResponse.PortOnePaymentResponse.PaymentData paymentData = 포트원결제조회(impUid, merchantUid);

        // 사용자 한테 자동으로 포인트 충전 처리
        user.chargePoint(paymentData.getAmount());

        // 결제 내역 저장
        // 객체 생성 (비영속상태)
        Payment payment = Payment.builder()
                .impUid(impUid)
                .merchantUid(merchantUid)
                .user(user)
                .amount(paymentData.getAmount())
                .status("paid")
                .build();

        paymentRepository.save(payment);

        return new PaymentResponse.VerifyDTO(paymentData.getAmount(), user.getPoint());
    }

    private PaymentResponse.PortOnePaymentResponse.PaymentData 포트원결제조회(String impUid, String merchantUid) {

        // 1. 엑세스 토큰 발급
        String accessToken = 포트원엑세스토큰발급();

        // 2. 포트원 자원서버에 결제 정보 조회 요청
        try {
            RestTemplate restTemplate = new RestTemplate();
            // 포트원 단건 조회 API (GET 방식과 헤더에 Bearer + "공백" accessToken)
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(accessToken);
            HttpEntity<Void> request = new HttpEntity<>(headers);

            ResponseEntity<PaymentResponse.PortOnePaymentResponse> response = restTemplate.exchange(
                    "https://api.iamport.kr/payments/" + impUid,
                    HttpMethod.GET,
                    request,
                    PaymentResponse.PortOnePaymentResponse.class
            );

            System.out.println("자원서버 - response : " + response);

            // 3. 응답 데이터 추출
            PaymentResponse.PortOnePaymentResponse.PaymentData data = response.getBody().getResponse();

            if (data == null) {
                throw new Exception400("결제 정보를 찾을 수 없습니다.");
            }

            // 4. ** 데이터 무결성 검증 **
            if (!"paid".equals(data.getStatus())) {
                throw new Exception400("결제가 완료되지 않았습니다.");
            }

            if (!merchantUid.equals(data.getMerchantUid())) {
                throw new Exception400("주문번호가 일치하지 않습니다.");
            }

            return data;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    private String 포트원엑세스토큰발급() {
        try {
            //https://api.iamport.kr/users/getToken
            RestTemplate restTemplate = new RestTemplate();

            // HTTP 메세지 헤더 생성
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // HTTP 메세지 바디 생성
            Map<String, String> body = new HashMap<>();
            // 포트원에서 발급 받았던 REST API KEY
            body.put("imp_key", impKey);
            body.put("imp_secret", impSecret);

            // header + body 결합
            HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);

            // 통신 요청
            ResponseEntity<PaymentResponse.PortOneTokenResponse> response = restTemplate.exchange(
                    "https://api.iamport.kr/users/getToken",
                    HttpMethod.POST,
                    request,
                    PaymentResponse.PortOneTokenResponse.class
            );

            // 응답받은 엑세스 토큰 리터
            return response.getBody().getResponse().getAccessToken();

        } catch (Exception e) {
            throw new Exception400("포트원 인증 실패: 관리자 설정을 확인하세요");
        }
    }

    public List<PaymentResponse.paymentListDTO> 결제내역조회(Long userId) {

        List<Payment> paymentList = paymentRepository.findAllByUserId(userId);

        return paymentList.stream()
                .map(PaymentResponse.paymentListDTO::new)
                .toList();
    }
}
