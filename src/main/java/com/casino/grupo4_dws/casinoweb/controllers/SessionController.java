package com.casino.grupo4_dws.casinoweb.controllers;

import com.casino.grupo4_dws.casinoweb.dto.BetDTO;
import com.casino.grupo4_dws.casinoweb.dto.PrizeDTO;
import com.casino.grupo4_dws.casinoweb.dto.UserDTO;
import com.casino.grupo4_dws.casinoweb.managers.UserManager;
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

    @PostConstruct
    public void init() {
        userManager.postConstruct();
    }

    @GetMapping("/login")
    public String loadLoginPage(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
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
            Optional<UserDTO> user = userManager.findByUsername(loginUsername);
            user.ifPresent(user1 -> session.setAttribute("user", user1));
            return "redirect:/NGames";
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Usuario o Contraseña incorrectos");
            return "redirect:/login";
        }
    }

    @GetMapping("/logout")
    public String loggingOut(HttpSession session) {
        UserDTO user = (UserDTO) session.getAttribute("user");
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
            redirectAttributes.addFlashAttribute("errorMessage","Rellena todos los campos");
            return "redirect:/register";
        }
        if (loginPassword == null || loginPassword.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage","Rellena todos los campos");
            return "redirect:/register";
        }
        try{
            userManager.saveUser(loginUsername, loginPassword);
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage",e.getMessage());
            return "redirect:/register";
        } catch (NullPointerException e) {
            redirectAttributes.addFlashAttribute("errorMessage",e.getMessage());
        }
        redirectAttributes.addFlashAttribute("message","Usuario registrado con exito");
        return "redirect:/login";
    }

//NO FUNCIONA ESTA PARTE, MIRAR CUANDO HAYAMOS AJUSTADO TODAS LAS ENTIDADES Y RELACIONES
@GetMapping("/user")
public String showUser(Model model, HttpSession session) {
    UserDTO user = (UserDTO) session.getAttribute("user");
    if (user == null) {
        return "redirect:/login";
    }

    // Get fresh user data from database
    user = userManager.findById(user.getId()).orElse(user);
    session.setAttribute("user", user);

    if (user.getInventory() == null) {
        user.setInventory(new ArrayList<>());
    }
    if (user.getBetHistory() == null) {
        user.setBetHistory(new ArrayList<>());
    }

    List<PrizeDTO> userInventory = user.getInventory();
    List<BetDTO> betHistory = user.getBetHistory().stream()
            .filter(BetDTO::isShow)
            .sorted((b1, b2) -> Long.compare(b2.getId(), b1.getId()))
            .collect(Collectors.toList());

    model.addAttribute("user", user);
    model.addAttribute("betHistory", betHistory);
    model.addAttribute("inventory", userInventory);
    return "staticLoggedIn/user";
}
}