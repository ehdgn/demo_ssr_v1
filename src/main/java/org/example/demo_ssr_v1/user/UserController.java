package org.example.demo_ssr_v1.user;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@RequiredArgsConstructor
@Controller
public class UserController {

    private final UserRepository userRepository;

    // 회원정보 수정 화면 요청
    // http://localhost:8080/user/update
    @GetMapping("/user/update")
    public String updateForm(Model model, HttpSession session) {
        // HttpServletRequest
        // A 사용자가 요청 시 --> 웹서버 --> 톰캣(WAS) Request 객체와 Response 객체를 만들어서
        // 스프링 컨테이너에게 전달

        // 1. 인증 검사 (o)
        // 2. 유효성 검사 (x)
        // 인증 검사를 하려면 세션 메모리에 접근해서 사용자의 정보가 있는지 없는지 여부 확인
        User sessionUser = (User) session.getAttribute("sessionUser");
        if (sessionUser == null) {
            System.out.println("로그인 하지 않은 사용자 입니다.");
            return "redirect:/login";
        }
        // 밑으로 온다면 로그인 한 사용자가 맞음
        User user = userRepository.findById(sessionUser.getId());
        model.addAttribute("user", user);

        return "user/update-form";
    }


    // 회원정보 수정 기능 요청 - 더티 체킹
    // http://localhost:8080/user/update
    @PostMapping("/user/update")
    public String updateProc(UserRequest.UpdateDTO updateDTO, HttpSession session) {
        // 1. 인증 검사 (o)
        User sessionUser = (User) session.getAttribute("sessionUser");
        if (sessionUser == null) {
            System.out.println("로그인 하지 않은 사용자 접근 막음");
            return "redirect:/login";
        }
        // 2. 유효성 검사 (o)
        // 3. 세션 메모리에 있던 기존 상태값을 변경 처리
        try {
            updateDTO.validate();
            User updateUser = userRepository.updateById(sessionUser.getId(), updateDTO);
            // 세션에 정보 갱신
            session.setAttribute("sessionUser", updateUser);
            // 수정 후 리다이렉트 처리 - 게시판 목록으로 이동
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
        // 1. 인증검사 (x) - 로그인 요청
        // 2. 유효성 검사 (o)
        // 3. db에 사용자 이름과 비밀번호 확인
        // 4. 로그인 성공 또는 실패 처리
        // 5. 웹 서버는 바보라서 사용자의 정보를 세션 메모리에 저장 시켜야
        //      다음 번 요청이 오더라도 알 수 있음. - 세션 저장 처리
        try {
            loginDTO.validate();
            User sessionUser = userRepository.findByUsernameAndPassword(
                    loginDTO.getUsername(),
                    loginDTO.getPassword());

            if (sessionUser == null)
                throw new IllegalArgumentException("사용자명 또는 비밀번호가 일치하지 않습니다.");

            // 세션에 저장
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
        // 1. 인증검사 (x) - 필요 없음 (회원가입)
        // 2. 유효성 검사 (o)
        // 3. 사용자 이름 중복체크
        // 4. 저장 요청

        joinDTO.validate();

        User existingUser = userRepository.findByUsername(joinDTO.getUsername());
        if (existingUser != null)
            throw new IllegalArgumentException("이미 존재하는 사용자 이름입니다.");

        User user = joinDTO.toEntity();
        userRepository.save(user);

        return "redirect:/login";
    }
}