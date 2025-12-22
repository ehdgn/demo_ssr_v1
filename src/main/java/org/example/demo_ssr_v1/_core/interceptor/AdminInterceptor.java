package org.example.demo_ssr_v1._core.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.example.demo_ssr_v1._core.errors.exception.Exception401;
import org.example.demo_ssr_v1._core.errors.exception.Exception403;
import org.example.demo_ssr_v1.user.User;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AdminInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 컨트롤러 들어가기 전에 조회 먼저 해야 함
        // 먼저 로그인이 되어 진 후 확인을 해야 함 (로그인 인터셉터 동작 하고 있음)
        HttpSession session = request.getSession();
        User sessionUser = (User) session.getAttribute("sessionUser");

        // 1. 로그인 체크는 loginInterceptor가 이미 했으므로 생략 가능 하지만
        //    안전상의 이유로 한번 더 체크 함
        if (sessionUser == null)
            throw new Exception401("로그인이 필요합니다.");

        // 관리자 역할 여부를 확인한다.
        if (!sessionUser.isAdmin())
            throw new Exception403("관리자 권한이 필요합니다.");

        return true;
    }
}
