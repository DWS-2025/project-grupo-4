package com.casino.grupo4_dws.casinoweb.controllers;

import com.casino.grupo4_dws.casinoweb.dto.BetDTO;
import com.casino.grupo4_dws.casinoweb.dto.PrizeDTO;
import com.casino.grupo4_dws.casinoweb.dto.UserDTO;
import com.casino.grupo4_dws.casinoweb.managers.UserManager;
import com.casino.grupo4_dws.casinoweb.mapper.BetMapper;
import com.casino.grupo4_dws.casinoweb.mapper.PrizeMapper;
import com.casino.grupo4_dws.casinoweb.mapper.UserMapper;
import com.casino.grupo4_dws.casinoweb.model.Bet;
import com.casino.grupo4_dws.casinoweb.model.Prize;
import com.casino.grupo4_dws.casinoweb.model.User;
import com.casino.grupo4_dws.casinoweb.managers.GameManager; // Inyectar GameManager
import com.casino.grupo4_dws.casinoweb.repos.UserRepository;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
public class SessionController {

    @Autowired
    UserManager userManager;
    @Autowired
    UserMapper userMapper;
    @Autowired
    BetMapper betMapper;
    @Autowired
    PrizeMapper prizeMapper;

    @PostConstruct
    public void init() {
        userManager.postConstruct();
    }

    @GetMapping("/login")
    public String loadLoginPage(HttpSession session) {
        Integer userId = (Integer) session.getAttribute("user");
        if (userId == null) {
            return "login";
        }
        Optional<UserDTO> userOp = userManager.findById(userId);
        if (userOp.isEmpty()) {
            return "login";
        } else {
            return "redirect:/";
        }
    }

    @PostMapping("/login")
    public String loginUser(Model model, @RequestParam String loginUsername, @RequestParam String loginPassword, HttpSession session, RedirectAttributes redirectAttributes) {
        if (loginUsername == null || loginUsername.trim().isEmpty()) {
            return "redirect:/login";
        }
        if (loginPassword == null || loginPassword.trim().isEmpty()) {
            return "redirect:/login";
        }
        if (userManager.isUserCorrect(loginUsername, loginPassword)) {
            User active = userManager.findByUsername(loginUsername).get();
            session.setAttribute("user", (Integer)active.getId());
            return "redirect:/NGames";
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Usuario o Contraseña incorrectos");
            return "redirect:/login";
        }
    }

    @GetMapping("/logout")
    public String loggingOut(HttpSession session) {
        Integer user = (Integer) session.getAttribute("user");
        if (user != null) {
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

    @PostMapping("/register")
    public String registerUser(Model model, @RequestParam String loginUsername, @RequestParam String loginPassword, RedirectAttributes redirectAttributes) {
        if (loginUsername == null || loginUsername.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Rellena todos los campos");
            return "redirect:/register";
        }
        if (loginPassword == null || loginPassword.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Rellena todos los campos");
            return "redirect:/register";
        }
        try {
            userManager.saveUser(loginUsername, loginPassword);
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/register";
        } catch (NullPointerException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        redirectAttributes.addFlashAttribute("message", "Usuario registrado con exito");
        return "redirect:/login";
    }

    @GetMapping("/user")
    public String showUser(Model model, HttpSession session) {
        Integer userId = (Integer) session.getAttribute("user");
        if (userId == null) {
            return "redirect:/login";
        }
        Optional<UserDTO> userOp = userManager.findById(userId);
        if (userOp.isEmpty()) {
            return "redirect:/login";
        }
        UserDTO userDTO = userOp.get();
        User user = userMapper.toEntity(userDTO);

        if (user.getInventory() == null) {
            user.setInventory(new ArrayList<>());
        }
        if (user.getBetHistory() == null) {
            user.setBetHistory(new ArrayList<>());
        }

        List<BetDTO> betHistory = betMapper.toDTOList(user.getBetHistory());
        List<PrizeDTO> userInventory = prizeMapper.toDTOList(user.getInventory());
        Collections.reverse(betHistory);
        model.addAttribute("user", userDTO);
        model.addAttribute("betHistory", betHistory);
        model.addAttribute("inventory", userInventory);
        return "staticLoggedIn/user";
    }
}