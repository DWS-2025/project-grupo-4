package com.casino.grupo4_dws.casinoweb.controllers;

import com.casino.grupo4_dws.casinoweb.model.Bet;
import com.casino.grupo4_dws.casinoweb.model.Prize;
import com.casino.grupo4_dws.casinoweb.model.User;
import com.casino.grupo4_dws.casinoweb.services.GameManager; // Inyectar GameManager
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
    private GameManager gameManager;

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
        user.setInventory(null);
        if ("admin".equals(loginUsername) && "admin".equals(loginPassword)) {
            user.setIsadmin(true);
        }

        session.setAttribute("user", user);
        return "redirect:/NGames";
    }

    @GetMapping("/logout")
    public String loggingOut(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user != null) {
            user.setIsadmin(false);
            user.setUserName("");
            user.setPassword("");
            user.setMoney(0);
            user.setInventory(null);
            session.removeAttribute("user");
        }
        return "redirect:/logoutConfirm";
    }

    @GetMapping("/logoutConfirm")
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
        if (user.getInventory() == null) {
            user.setInventory(new ArrayList<>());
        }
        if (user.getBetHistory() == null) {
            user.setBetHistory(new ArrayList<>());
        }

        List<Prize> userInventory = user.getInventory();
        List<Bet> betHistory = user.getBetHistory();

        if (!betHistory.isEmpty()) {
            Collections.reverse(betHistory);
        }

        model.addAttribute("user", user);
        model.addAttribute("betHistory", betHistory);
        model.addAttribute("Inventory", userInventory);
        return "staticLoggedIn/user";
    }
}