package org.example.demo_ssr_v1.user;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * 사용자 Controller (표현 계층)
 * 핵심 개념:
 * - HTTP 요청을 받아서 처리
 * - 요청 데이터 검증 및 파라미터 바인딩
 * - Service 레이어에 비즈니스 로직을 위임
 * - 응답 데이터를 View에 전달함
 *
 */

@RequiredArgsConstructor
@Controller
public class UserController {

    private final UserService userService;

    // 회원정보 수정 화면 요청
    // http://localhost:8080/user/update
    @GetMapping("/user/update")
    public String updateForm(Model model, HttpSession session) {

        User sessionUser = (User) session.getAttribute("sessionUser");

        User user = userService.회원정보수정화면(sessionUser.getId());
        model.addAttribute("user", user);
        return "user/update-form";
    }


    // 회원정보 수정 기능 요청 - 더티 체킹
    // http://localhost:8080/user/update
    @PostMapping("/user/update")
    public String updateProc(UserRequest.UpdateDTO updateDTO, HttpSession session) {

        User sessionUser = (User) session.getAttribute("sessionUser");

        try {
            // 유효성 검사 (형식 검사)
            updateDTO.validate();
            User updateUser = userService.회원정보수정(updateDTO, sessionUser.getId());
            // 회원 정보 수정은 - 세션 갱신해 주어야 한다.
            session.setAttribute("sessionUser", updateUser);
            return "redirect:/";
        } catch (Exception e) {
            return "user/update-form";
        }

    }


    // 로그아웃 기능 요청
    // http://localhost:8080/logout
    @GetMapping("/logout")
    public String logout(HttpSession session) {

        // 세션 무효화
        session.invalidate();
        return "redirect:/";
    }

    // 로그인 화면 요청
    // http://localhost:8080/login
    @GetMapping("/login")
    public String loginForm() {
        return "user/login-form";
    }

    // JWT 토큰 기반 인증 x -> 세션 기반 인증 처리
    // 로그인 기능 요청
    // http://localhost:8080/login
    @PostMapping("/login")
    public String loginProc(UserRequest.LoginDTO loginDTO, HttpSession session) {

        try {
            // 유효성 검사
            loginDTO.validate();
            User sessionUser = userService.로그인(loginDTO);
            session.setAttribute("sessionUser", sessionUser);
            return "redirect:/";

        } catch (Exception e) {
            // 로그인 실패 시 다시 로그인 화면으로 처리
            return "user/login-form";
        }
    }

    // 회원가입 화면 요청
    // http://localhost:8080/join
    @GetMapping("/join")
    public String joinForm() {
        return "user/join-form";
    }

    // 회원가입 기능 요청
    // http://localhost:8080/join
    @PostMapping("/join")
    public String joinProc(UserRequest.JoinDTO joinDTO) {
        joinDTO.validate();
        userService.회원가입(joinDTO);
        return "redirect:/login";
    }
}