package org.example.demo_ssr_v1.user;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.example.demo_ssr_v1._core.errors.exception.Exception401;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 사용자 Controller (표현 계층)
 * 핵심 개념:
 * - HTTP 요청을 받아서 처리
 * - 요청 데이터 검증 및 파라미터 바인딩
 * - Service 레이어에 비즈니스 로직을 위임
 * - 응답 데이터를 View에 전달함
 */

@RequiredArgsConstructor
@Controller
public class UserController {

    private final UserService userService;

    // /user/point/charge
    @GetMapping("/user/point/charge")
    public String chargePointForm(Model model, HttpSession session){

        User sessionUser = (User) session.getAttribute("sessionUser");

        model.addAttribute("user", sessionUser);
        return "user/charge-point";
    }

    @GetMapping("/user/kakao")
    public String kakaoCallback(@RequestParam(name = "code") String code, HttpSession session) {

        try {
            // 서비스 단에 비즈니스 로직을 위임 처리
            User user = userService.카카오소셜로그인(code);
            // 세션 정보에 사용자 정보 저장
            session.setAttribute("sessionUser", user);
            return "redirect:/";
        } catch (Exception e) {
            System.out.println("소셜 로그인 실패 " + e.getMessage());
            // alert 창 (에러메세지) --> 로그인 페이지로 이동 처리
            throw new Exception401(e.getMessage());
        }
    }

    // 프로필 이미지 삭제하기
    @PostMapping("/user/profile-image/delete")
    public String deleteProfileImage(HttpSession session) {
        User sessionUser = (User) session.getAttribute("sessionUser");

        User updateUser = userService.프로필이미지삭제(sessionUser.getId());
        // 왜 user를 다시 받을까? -- 세션 정보가 (즉 프로필이 삭제 되었기 때문에)
        // 세션 정보 갱신 처리 해주기 위함
        session.setAttribute("sessionUser", updateUser); // 세션 정보 갱신

        // 일반적으로 POST 요청이 오면 PRG 패턴으로 설계 됨
        // POST -> Redirect 처리 -> GET 요청
        return "redirect:/user/detail";
    }

    // 회원정보 수정 화면 요청
    // http://localhost:8080/user/update
    @GetMapping("/user/update")
    public String updateForm(Model model, HttpSession session) {

        User sessionUser = (User) session.getAttribute("sessionUser");

        User user = userService.회원정보수정화면(sessionUser.getId());
        model.addAttribute("user", user);
        return "user/update-form";
    }

    // 마이페이지
    // http://localhost:8080/user/detail
    @GetMapping("/user/detail")
    public String detailForm(Model model, HttpSession session) {
        User sessionUser = (User) session.getAttribute("sessionUser");

        User user = userService.마이페이지(sessionUser.getId());

        model.addAttribute("user", user);
        return "user/detail";
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
            return "redirect:/user/detail";
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