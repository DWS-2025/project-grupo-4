package com.casino.grupo4_dws.casinoweb.controllers;

import com.casino.grupo4_dws.casinoweb.model.Bet;
import com.casino.grupo4_dws.casinoweb.model.Game;
import com.casino.grupo4_dws.casinoweb.model.Prize;
import com.casino.grupo4_dws.casinoweb.model.User;
import com.casino.grupo4_dws.casinoweb.services.GameManager; // Inyectar GameManager
import com.casino.grupo4_dws.casinoweb.services.UserManager;  // Inyectar UserManager
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Controller
public class SessionController {

    @Autowired
    private UserManager userManager;  // Inyectar UserManager

    @Autowired
    private GameManager gameManager;  // Inyectar GameManager para obtener los juegos disponibles

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
    public String loggingOut(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user != null) {
            user.setIsadmin(false);
            user.setUserName("");
            user.setPassword("");
            user.setMoney(0);
            user.setInventario(null);
            session.removeAttribute("user");
        }
        return "redirect:/logoutConfirmar";
    }

    @GetMapping("/logoutConfirmar")
    public String logoutConfirmar(HttpSession session) {
        return "redirect:/";
    }

    @GetMapping("/register")
    public String registerUser() {
        return "register";
    }


    @GetMapping("/user")
    public String showUser(Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        model.addAttribute("user", user);
        if(user.getInventario() == null) {
            user.setInventario(new ArrayList<>());
        }
        if(user.getBetHistory() == null) {
            user.setBetHistory(new ArrayList<>());
        }

        List<Prize> inventario = user.getInventario();
        List<Bet> betHistory = user.getBetHistory();

        if(!betHistory.isEmpty()) {
            Collections.reverse(betHistory);
        }

        model.addAttribute("user", user);
        model.addAttribute("betHistory", betHistory);
        model.addAttribute("inventario", inventario);
        return "staticLoggedIn/user";
    }
}
