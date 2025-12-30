package org.example.demo_ssr_v1._core.utils;

import java.util.Random;

public class MailUtils {

    // 정적 메서드로 랜덤 번호 6자리 생성하는 헬프 메서드
    public static String generateRandomCode() {
        Random random = new Random();
        // 0 ~ 899999 (하나의 랜덤 숫자 생성)
        int code = 100000 + random.nextInt(900000);

        return String.valueOf(code);
    }
}
