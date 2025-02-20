package com.casino.grupo4_dws.casinoweb.Controllers;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class indexcontroller {

    // Este método maneja las solicitudes a la URL '/'

    @GetMapping("/")
    public String inicio(Model model, HttpSession session) {
        String loginUsername = (String) session.getAttribute("loginUsername");
        if (loginUsername == null || loginUsername.trim().isEmpty()){
            return "inicio";
        }
        model.addAttribute("name",loginUsername);
        return "staticLoggedIn/loggedMain";
    }

    @GetMapping("/NJuegos")
    public String mostrarJuegos(Model model, HttpSession session) {
        String loginUsername = (String) session.getAttribute("loginUsername");
        model.addAttribute("name",loginUsername);
        if (loginUsername == null || loginUsername.trim().isEmpty()){
            return "NJuegos";
        }
        return "staticLoggedIn/loggedGames";
    }

    @GetMapping("/crash")
    public String goCrash() {
        return "crash"; // Página crash.html
    }

    @GetMapping("/login")
    public String loadLoginPage() {
        return "login";
    }
    @PostMapping("/login")
    public String loginUser(Model model, @RequestParam String loginUsername, HttpSession session) {
        if (loginUsername == null || loginUsername.trim().isEmpty()){
            return "redirect:/login";
        }
        session.setAttribute("loginUsername", loginUsername);
        return "redirect:/";
    }
    @GetMapping("/logout")
    public String loggingOut(HttpSession session, HttpServletRequest request) {
        String urlAnterior = request.getHeader("Referer");
        session.setAttribute("urlAntesDeLogout", urlAnterior);
        session.removeAttribute("loginUsername");
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




