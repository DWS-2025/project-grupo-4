package com.casino.grupo4_dws.casinoweb.controllers;

import com.casino.grupo4_dws.casinoweb.dto.UserDTO;
import com.casino.grupo4_dws.casinoweb.managers.UserManager;
import com.casino.grupo4_dws.casinoweb.mapper.UserMapper;
import com.casino.grupo4_dws.casinoweb.security.CSRFService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
public class AdminController {

    @Autowired
    private UserManager userManager;
    @Autowired
    private UserMapper userMapper;

    @GetMapping("/admin")
    public String showAdminPanel(Model model, HttpSession session, HttpServletRequest request) {
        String csrfToken = CSRFService.getCSRFToken(request);
        if (csrfToken == null) {
            CSRFService.setCSRFToken(request);
            csrfToken = CSRFService.getCSRFToken(request);
        }

        model.addAttribute("csrfToken", csrfToken);
        Integer userId = (Integer) session.getAttribute("user");

        if (userId == null) {
            return "redirect:/login";
        }

        Optional<UserDTO> userOp = userManager.findById(userId);
        if (userOp.isEmpty()) {
            return "redirect:/login";
        }

        if (userManager.isAdmin(userOp.get())) {
            List<UserDTO> userList = userManager.getUserList();

            model.addAttribute("user", userOp.get());
            model.addAttribute("userList", userList);

            return "/staticLoggedIn/admin";
        } else {
            // Si NO es admin, redirige a la página principal
            return "redirect:/";
        }
    }
}