package com.casino.grupo4_dws.casinoweb.controllers;


import com.casino.grupo4_dws.casinoweb.model.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class SessionController {
    @Autowired
    private User user;

    @GetMapping("/login")
    public String loadLoginPage() {
        return "login";
    }
    @PostMapping("/login")
    public String loginUser(Model model, @RequestParam String loginUsername, @RequestParam String loginPassword, HttpSession session) {
        if (loginUsername == null || loginUsername.trim().isEmpty()) {
            return "redirect:/login";
        }

        User user = new User();
        user.setUserName(loginUsername);
        user.setPassword(loginPassword);
        user.setMoney(5000);
        user.setInventario(null);
        if ("admin".equals(loginUsername) && "admin".equals(loginPassword)) {
            user.setIsadmin(true);
        }

        session.setAttribute("user", user);
        return "redirect:/";
    }

    @GetMapping("/logout")
    public String loggingOut(HttpSession session, HttpServletRequest request) {
        User user = (User) session.getAttribute("user");
        if (user != null) {
            user.setIsadmin(false);
            user.setUserName("");
            user.setPassword("");
            user.setMoney(0);
            user.setInventario(null);
            session.removeAttribute("user");
        }

        String urlAnterior = request.getHeader("Referer");
        session.setAttribute("urlAntesDeLogout", urlAnterior);
        return "redirect:/logoutConfirmar";
    }
    @GetMapping("/logoutConfirmar")
    public String logoutConfirmar(HttpSession session) {
        String urlAnterior = (String) session.getAttribute("urlAntesDeLogout");
        session.removeAttribute("urlAntesDeLogout");
        return "redirect:" + (urlAnterior != null ? urlAnterior : "/");
    }
    @GetMapping("/register")
    public String registerUser() {
        return "register";
    }
}
