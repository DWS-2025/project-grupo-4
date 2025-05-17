package com.casino.grupo4_dws.casinoweb.controllers;

import com.casino.grupo4_dws.casinoweb.dto.BetDTO;
import com.casino.grupo4_dws.casinoweb.dto.PrizeDTO;
import com.casino.grupo4_dws.casinoweb.dto.UserDTO;
import com.casino.grupo4_dws.casinoweb.managers.UserManager;
import com.casino.grupo4_dws.casinoweb.mapper.BetMapper;
import com.casino.grupo4_dws.casinoweb.mapper.PrizeMapper;
import com.casino.grupo4_dws.casinoweb.mapper.UserMapper;
import com.casino.grupo4_dws.casinoweb.model.User;
import com.casino.grupo4_dws.casinoweb.managers.CSRFManager;
import com.casino.grupo4_dws.casinoweb.security.CSRFValidator;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

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
    public String loadLoginPage(HttpSession session, HttpServletRequest request, Model model) {

        String csrfToken = CSRFManager.getCSRFToken(request);
        if (csrfToken == null) {
            CSRFManager.setCSRFToken(request);
            csrfToken = CSRFManager.getCSRFToken(request);
        }

        model.addAttribute("csrfToken", csrfToken);

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
    public String loginUser(Model model, @RequestParam String loginUsername, @RequestParam String loginPassword, HttpSession session, RedirectAttributes redirectAttributes,
                            HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Validate CSRF TOKEN
        if (!CSRFValidator.validateCSRFToken(request)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            redirectAttributes.addFlashAttribute("errorMessage", "CSRF token invalid");
            return "redirect:/login";
        }

        if (loginUsername == null || loginUsername.trim().isEmpty()) {
            return "redirect:/login";
        }
        if (loginPassword == null || loginPassword.trim().isEmpty()) {
            return "redirect:/login";
        }
        try {
            if (userManager.isUserCorrect(loginUsername, loginPassword)) {
                User active = userManager.findByUsername(loginUsername).get();
                session.setAttribute("user", (Integer) active.getId());
                CSRFManager.regenerateCSRFToken(request);
                return "redirect:/NGames";
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Usuario o Contraseña incorrectos");
                return "redirect:/login";
            }
        } catch (IllegalArgumentException e) {
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
    public String registerUser(Model model, HttpServletRequest request) {
        String csrfToken = CSRFManager.getCSRFToken(request);
        model.addAttribute("csrfToken", csrfToken);
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(Model model, @RequestParam String loginUsername, @RequestParam String loginPassword, RedirectAttributes redirectAttributes,
                               HttpServletRequest request, HttpServletResponse response) {
        if (!CSRFValidator.validateCSRFToken(request)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            redirectAttributes.addFlashAttribute("errorMessage", "CSRF token invalid");
            return "redirect:/register";
        }

        if (loginUsername == null || loginUsername.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Rellena todos los campos");
            return "redirect:/register";
        }
        if (loginPassword == null || loginPassword.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Rellena todos los campos");
            return "redirect:/register";
        }
        if (loginPassword.length() < 4 || loginPassword.length() > 16) {
            redirectAttributes.addFlashAttribute("errorMessage", "La contraseña debe tener entre 4 y 16 caracteres");
        }
        try {
            userManager.saveUser(loginUsername, loginPassword);
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/register";
        } catch (NullPointerException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        CSRFManager.regenerateCSRFToken(request);
        redirectAttributes.addFlashAttribute("message", "Usuario registrado con exito");
        return "redirect:/login";
    }

    @GetMapping("/user")
    public String showUser(Model model, HttpSession session, HttpServletRequest request) {
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

    @PostMapping("/deleteUser/{id}")
    public String deleteUser(
            @PathVariable Integer id, HttpSession session, RedirectAttributes redirectAttributes, HttpServletRequest request, HttpServletResponse response) {

        if (!CSRFValidator.validateCSRFToken(request)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            redirectAttributes.addFlashAttribute("errorMessage", "CSRF token invalid");
            return "redirect:/register";
        }

        Integer adminId = (Integer) session.getAttribute("user");
        if (adminId == null) {
            return "redirect:/login";
        }

        Optional<UserDTO> adminOp = userManager.findById(adminId);
        if (adminOp.isEmpty() || !userManager.isAdmin(adminOp.get())) {
            redirectAttributes.addFlashAttribute("errorMessage", "No tienes permisos de administrador.");
            return "redirect:/user";
        }

        if (id.equals(adminId)) {
            redirectAttributes.addFlashAttribute("errorMessage", "No puedes eliminarte a ti mismo.");
            return "redirect:/admin";
        }

        Optional<UserDTO> userToDeleteOp = userManager.findById(id);
        if (userToDeleteOp.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "El usuario no existe.");
            return "redirect:/admin";
        }

        userManager.deleteUser(userToDeleteOp.get(), adminOp.get());
        redirectAttributes.addFlashAttribute("successMessage", "Usuario eliminado correctamente.");
        CSRFManager.regenerateCSRFToken(request);
        return "redirect:/admin";  // Recargar el panel de admin
    }

    @GetMapping("/updateUser/{id}")
    public String editUser(Model model, @PathVariable Integer id, HttpSession session,
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
            return "staticLoggedIn/updateUserForm";
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "No puedes realizar esta accion");
            return "redirect:/";
        }
    }

    @PostMapping("/updateUser/{id}")
    public String updateUser(Model model, @PathVariable Integer id, HttpSession session,
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
            redirectAttributes.addFlashAttribute("errorMessage", "Usuario a actualizar no encontrado");
            return "redirect:/";
        }
        if (userManager.isAdmin(userOp.get()) || user2op.get().equals(userOp.get())) {
            userManager.updateUser(updatedUser, id);
            redirectAttributes.addFlashAttribute("message", "Usuario actualizado con exito");
            CSRFManager.regenerateCSRFToken(request);
            return "redirect:/user";
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "No puedes realizar esta accion");
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

    @PostMapping("/autodeleteUser")
    public String deleteMySelf(HttpSession session, HttpServletRequest request, RedirectAttributes redirectAttributes, HttpServletResponse response) {
        if (!CSRFValidator.validateCSRFToken(request)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            redirectAttributes.addFlashAttribute("errorMessage", "CSRF token invalid");
            return "redirect:/register";
        }
        Integer userId = (Integer) session.getAttribute("user");
        if (userId == null) {
            return "redirect:/";
        }
        Optional<UserDTO> userOp = userManager.findById(userId);
        if (userOp.isEmpty()) {
            return "redirect:/";
        }
        session.removeAttribute("user");
        userManager.deleteUser(userId);
        CSRFManager.regenerateCSRFToken(request);
        return "redirect:/";
    }

    @PostMapping("/users/{id}/upload-document")
    public String uploadDocument(@PathVariable("id") int userId,
                                 @RequestParam("document") MultipartFile document,
                                 RedirectAttributes redirectAttributes,
                                 HttpServletRequest request, HttpServletResponse response) {

        if (!CSRFValidator.validateCSRFToken(request)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            redirectAttributes.addFlashAttribute("errorMessage", "CSRF token invalid");
            return "redirect:/updateUser/" + userId;
        }

        try {
            userManager.saveUserDocument(userId, document);
            redirectAttributes.addFlashAttribute("successMessage", "Documento subido con éxito.");
            CSRFManager.regenerateCSRFToken(request);
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "Error al subir el documento.");
        }
        return "redirect:/user";
    }

    @GetMapping("/users/{id}/view-document")
    public ResponseEntity<byte[]> viewDocument(@PathVariable("id") int userId, HttpSession session) {
        Integer id = (Integer) session.getAttribute("user");
        if (id == null) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        Optional<UserDTO> userOp = userManager.findById(id);
        if (userOp.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        UserDTO user = userOp.get();
        if (userManager.isAdmin(user) || id == userId) {
            try {
                String documentPath = userManager.getUserDocumentPath(userId);
                File file = new File(documentPath);

                if (!file.exists() || !file.canRead()) {
                    return ResponseEntity.notFound().build();
                }

                byte[] fileContent = Files.readAllBytes(file.toPath());

                String contentType = Files.probeContentType(file.toPath());
                if (contentType == null) {
                    contentType = "application/octet-stream";
                }

                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(contentType))
                        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + file.getName() + "\"")
                        .body(fileContent);

            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        } else {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

}