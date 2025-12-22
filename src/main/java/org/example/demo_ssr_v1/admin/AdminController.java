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

}
