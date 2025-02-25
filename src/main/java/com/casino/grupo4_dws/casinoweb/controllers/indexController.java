package com.casino.grupo4_dws.casinoweb.controllers;


import com.casino.grupo4_dws.casinoweb.model.Prize;
import com.casino.grupo4_dws.casinoweb.model.User;
import com.casino.grupo4_dws.casinoweb.services.GameManager;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class indexController {
    @Autowired
    private User user;
    private final GameManager Services;

    @Autowired
    public indexController(GameManager services) {
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

    @GetMapping("/user")
    public String showUser(Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        model.addAttribute("user", user);
        List<Prize> inventario = user.getInventario();
        model.addAttribute("inventario", inventario);
        return "staticLoggedIn/user";
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





