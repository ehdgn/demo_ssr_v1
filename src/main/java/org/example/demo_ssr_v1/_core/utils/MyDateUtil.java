package org.example.demo_ssr_v1._core.utils;

import org.springframework.format.annotation.DateTimeFormat;

import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;


public class MyDateUtil {
    @DateTimeFormat(pattern = "yyyy-MM-dd")

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    // 정적 메서드 (기능) 시간 포맷터
    public static String setNewTimeStamp(Timestamp time) {
        // timestamp 받아서 변환
        if (time == null) return null;

        return time.toLocalDateTime().toString();
    }
}

