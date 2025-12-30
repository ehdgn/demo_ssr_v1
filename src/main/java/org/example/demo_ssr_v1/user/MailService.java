package org.example.demo_ssr_v1.user;

import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.example.demo_ssr_v1._core.utils.MailUtils;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class MailService {

    // 의존성 주입 받았던 클래스 (JavaMailSender - 편지를 쓰게하는 클래스)
    private final JavaMailSender javaMailSender;
    private final HttpSession session;

    public void 인증번호발송(String email) {
        // 1. 인증 번호 생성
        // email -> 인증번호(6자리) -> 임시로 세션메모리에 저장 -> 메일 발송 요청
        String code = MailUtils.generateRandomCode();

        // 2. 이메일 전송 내용 설정
        // SimpleMailMessage - 순수하게 텍스트만 보낼 때 사용
        // MimeMessage - 텍스트 뿐만 아니라 HTML, 첨부파일, 포함할 수 있는 표준 포맷
        MimeMessage message = javaMailSender.createMimeMessage();

        // 3. 구글 메일 서버로 전송 - 우리 서버가 아니고 외부 서버로 통신 요청 (...)
        // 외부로 통신하는 코드 일 때도 기본적으로 try catch를 사용해줘야 한다.
        try {
            // 3.1 도우미 객체를 사용
            // true: 멀티파트 허용
            // "UTF-8": 인코딩 설정
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(email); // 받는 사람 이메일 주소
            helper.setSubject("[MyBlog] 회원가입 이메일 전송");
            helper.setText("<h3>인증번호는 [" + code + "] 입니다.</h3>", true);

            javaMailSender.send(message);

            // 4. 세션에 임시 코드 저장
            // sessionUser : User(...)
            // code_a@naver.com: 123456
            // 동시에 접속자가 많아도 이메일 주소로 누구의 인증번호인지 구별할 수 있음
            session.setAttribute("code_" + email, code);

        } catch (Exception e) {

        }
    }

    public boolean 인증번호확인(String email, String code) {
        // 1. 세션에서 저장된 코드 가져오기
        // key: code_ + "a@naver.com"
        String savedCode = (String) session.getAttribute("code_" + email);

        // 2. 세션에 가지고 온 code 값과 사용자가 입력한 인증번호 일치 여부 확인
        if (savedCode != null && savedCode.equals(code)) {
            // 세션 메모리에서 제거 해야함
            session.removeAttribute("code_" + email);
            return true;
        }
        return false;
    }
}
