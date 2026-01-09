package org.example.demo_ssr_v1.admin;

import jakarta.servlet.http.HttpSession;
import org.example.demo_ssr_v1.user.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminController {

    // http://localhost:8080/admin/dashboard
    @GetMapping("/admin/dashboard")
    public String dashBoard(HttpSession session, Model model) {

        User sessionUser = (User) session.getAttribute("sessionUser");

        model.addAttribute("sessionUser", sessionUser);
        return "admin/dashboard";
    }

    // 관리자 환불 요청 관리 목록 페이지
    @GetMapping("/admin/refund/list")
    public String refundManagement(Model model) {
        // 서비스에게 데이터 요청
        // 화면에 뿌려 줌

        return "admin/admin-refund-list";
    }

}
