package com.casino.grupo4_dws.casinoweb.controllers;


import com.casino.grupo4_dws.casinoweb.model.User;
import com.casino.grupo4_dws.casinoweb.services.GameManager;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class indexcontroller {
    @Autowired
    private User user;
    private final GameManager Services;

    @Autowired
    public indexcontroller(GameManager services) {
        Services = services;
    }

    @GetMapping("/")
    public String inicio(Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "inicio";
        }
        model.addAttribute("user", user);
        return "staticLoggedIn/loggedMain";
    }

    @GetMapping("/prizes")
    public String showPrizes(Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "prizes";
        }
        model.addAttribute("user", user);
        return "staticLoggedIn/loggedPrizes";
    }

    @GetMapping("/crash")
    public String goCrash() {
        return "crash"; // Página crash.html
    }

    @GetMapping("/rule")
    public String goRule() {
        return "rule"; // Página rule.html
    }

    @GetMapping("/slots")
    public String goSlots() {
        return "slots"; // Página slots.html
    }

}





