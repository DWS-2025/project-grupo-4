package com.casino.grupo4_dws.casinoweb.controllers;

import com.casino.grupo4_dws.casinoweb.dto.UserDTO;
import com.casino.grupo4_dws.casinoweb.managers.UserManager;
import com.casino.grupo4_dws.casinoweb.managers.CSRFManager;
import com.casino.grupo4_dws.casinoweb.security.CSRFValidator;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
public class AdminController {

    @Autowired
    private UserManager userManager;

    @GetMapping("/admin")
    public String showAdminPanel(Model model, HttpSession session, HttpServletRequest request) {
        String csrfToken = CSRFManager.getCSRFToken(request);
        if (csrfToken == null) {
            CSRFManager.setCSRFToken(request);
            csrfToken = CSRFManager.getCSRFToken(request);
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

    @GetMapping("/admin/userInventory/{id}")
    public String showUserInventory(Model model, @PathVariable int id, HttpSession session, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        String csrfToken = CSRFManager.getCSRFToken(request);
        if (csrfToken == null) {
            CSRFManager.setCSRFToken(request);
            csrfToken = CSRFManager.getCSRFToken(request);
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
            Optional<UserDTO> userOp2 = userManager.findById(id);
            if (userOp2.isEmpty()) {
                return "redirect:/admin";
            }
            model.addAttribute("activeUser", userOp.get());
            model.addAttribute("userCheckInfo", userOp2.get());
            model.addAttribute("userCheckInventory", userManager.returnDTOInventory(userOp2.get()));
            return "/staticLoggedIn/adminUserInventory";
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "No puedes realizar esa acción");
            return "redirect:/";
        }
    }

    @GetMapping("/updateUserAdmin/{id}")
    public String editUserAdmin(Model model, @PathVariable Integer id, HttpSession session,
                                RedirectAttributes redirectAttributes, HttpServletRequest request) {

        String csrfToken = CSRFManager.getCSRFToken(request);
        if (csrfToken == null) {
            CSRFManager.setCSRFToken(request);
            csrfToken = CSRFManager.getCSRFToken(request);
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
        Optional<UserDTO> user2op = userManager.findById(id);
        if (user2op.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Usuario a eliminar no encontrado");
            return "redirect:/";
        }
        if (userManager.isAdmin(userOp.get()) || user2op.get().equals(userOp.get())) {
            model.addAttribute("editUser", user2op.get());
            return "staticLoggedIn/updateUserFormAdmin";
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "No puedes realizar esta accion");
            return "redirect:/";
        }
    }

    @PostMapping("/updateUserAdmin/{id}")
    public String updateUserAdmin(Model model, @PathVariable Integer id, HttpSession session,
                                  RedirectAttributes redirectAttributes, @ModelAttribute("editUser") UserDTO updatedUser,
                                  HttpServletRequest request, HttpServletResponse response) {
        if (!CSRFValidator.validateCSRFToken(request)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            redirectAttributes.addFlashAttribute("errorMessage", "CSRF token invalid");
            return "redirect:/register";
        }
        Integer userId = (Integer) session.getAttribute("user");
        if (userId == null) {
            return "redirect:/login";
        }
        Optional<UserDTO> userOp = userManager.findById(userId);
        if (userOp.isEmpty()) {
            return "redirect:/login";
        }
        Optional<UserDTO> user2op = userManager.findById(id);
        if (user2op.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Usuario no encontrado");
            return "redirect:/";
        }
        if (userManager.isAdmin(userOp.get()) || user2op.get().equals(userOp.get())) {
            userManager.updateUserAdmin(updatedUser, id);
            redirectAttributes.addFlashAttribute("message", "Usuario actualizado con exito");
            CSRFManager.regenerateCSRFToken(request);
            return "redirect:/user";
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "No puedes realizar esta accion");
            return "redirect:/";
        }
    }
}