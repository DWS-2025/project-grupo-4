package com.casino.grupo4_dws.casinoweb.controllers;


import com.casino.grupo4_dws.casinoweb.dto.UserDTO;
import com.casino.grupo4_dws.casinoweb.managers.UserManager;
import com.casino.grupo4_dws.casinoweb.managers.GameManager;
import com.casino.grupo4_dws.casinoweb.managers.CSRFManager;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Optional;

@Controller
public class IndexController {

    private final GameManager Services;

    @Autowired
    public IndexController(GameManager services) {
        Services = services;
    }

    @Autowired
    public UserManager userManager;

    @GetMapping("/")
    public String inicio(Model model, HttpSession session, HttpServletRequest request) {
        String csrfToken = CSRFManager.getCSRFToken(request);
        if (csrfToken == null) {
            CSRFManager.setCSRFToken(request);
            csrfToken = CSRFManager.getCSRFToken(request);
        }
        Integer userId = (Integer) session.getAttribute("user");
        if (userId == null) {
            return "inicio";
        }
        Optional<UserDTO> userOp = userManager.findById(userId);
        if (userOp.isEmpty()) {
            return "inicio";
        } else {
            model.addAttribute("user", userOp.get());
            return "staticLoggedIn/loggedMain";
        }
    }
}





