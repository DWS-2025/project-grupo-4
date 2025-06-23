package com.casino.grupo4_dws.casinoweb.controllers;


import com.casino.grupo4_dws.casinoweb.dto.GameDTO;
import com.casino.grupo4_dws.casinoweb.dto.UserDTO;
import com.casino.grupo4_dws.casinoweb.managers.UserManager;
import com.casino.grupo4_dws.casinoweb.mapper.GameMapper;
import com.casino.grupo4_dws.casinoweb.model.Game;
import com.casino.grupo4_dws.casinoweb.managers.CSRFManager;
import com.casino.grupo4_dws.casinoweb.security.CSRFValidator;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import com.casino.grupo4_dws.casinoweb.managers.GameManager;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.Optional;

import java.util.ArrayList;
import java.util.List;

@Controller
public class GamesController {

    @Autowired
    private GameManager gameManager;
    @Autowired
    private GameMapper gameMapper;
    @Autowired
    private UserManager userManager;

    @PostConstruct
    public void init() {
        try {
            gameManager.postConstruct();
        } catch (IOException | SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/NGames")
    public String showGames(Model model, HttpSession session, HttpServletRequest request) {
        String csrfToken = CSRFManager.getCSRFToken(request);
        if (csrfToken == null) {
            CSRFManager.setCSRFToken(request);
            csrfToken = CSRFManager.getCSRFToken(request);
        }

        model.addAttribute("csrfToken", csrfToken);
        List<GameDTO> games = new ArrayList<>(gameManager.getGameList());
        model.addAttribute("games", games);
        Integer userId = (Integer) session.getAttribute("user");
        if (userId == null) {
            return "NGames";
        }
        Optional<UserDTO> userOp = userManager.findById(userId);
        if (userOp.isEmpty()) {
            return "NGames";
        }
        model.addAttribute("user", userOp.get());
        return "staticLoggedIn/loggedGames";
    }


    @GetMapping("/NGames/mostLiked")
    public String mostLiked(Model model, HttpSession session, HttpServletRequest request) {
        String csrfToken = CSRFManager.getCSRFToken(request);
        if (csrfToken == null) {
            CSRFManager.setCSRFToken(request);
            csrfToken = CSRFManager.getCSRFToken(request);
        }

        model.addAttribute("csrfToken", csrfToken);

        List<GameDTO> games = new ArrayList<>(gameManager.getGameListMostLiked());

        model.addAttribute("mostLikedGames", games);
        Integer userId = (Integer) session.getAttribute("user");
        if (userId == null) {
            return "redirect:/NGames";
        }
        Optional<UserDTO> userOp = userManager.findById(userId);
        if (userOp.isEmpty()) {
            return "redirect:/NGames";
        }
        model.addAttribute("user", userOp.get());
        return "GamesMostLiked";
    }

    @GetMapping("/NGames/liked")
    public String liked(Model model, HttpSession session, HttpServletRequest request) {
        String csrfToken = CSRFManager.getCSRFToken(request);
        if (csrfToken == null) {
            CSRFManager.setCSRFToken(request);
            csrfToken = CSRFManager.getCSRFToken(request);
        }

        model.addAttribute("csrfToken", csrfToken);

        Integer userId = (Integer) session.getAttribute("user");
        if (userId == null) {
            return "redirect:/NGames";
        }
        Optional<UserDTO> userOp = userManager.findById(userId);
        if (userOp.isEmpty()) {
            return "redirect:/NGames";
        }
        UserDTO user = userOp.get();
        List<GameDTO> favGames = userManager.getFavGames(user);

        model.addAttribute("games", favGames);
        model.addAttribute("user", user);

        return "GamesMyLiked";
    }

    @GetMapping("/add")
    public String addGameForm(Model model, HttpSession session, HttpServletRequest request) {
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
        if (!userManager.isAdmin(userOp.get())) {
            return "redirect:/login";
        }
        model.addAttribute("newGame", new Game());
        return "staticLoggedIn/addGameForm";
    }


    @PostMapping("/add")
    public String addGame(@ModelAttribute Game newGame, @RequestParam("imageFile") MultipartFile imageFile,
                          RedirectAttributes redirectAttributes, HttpSession session,
                          HttpServletRequest request, HttpServletResponse response) throws IOException {

        if (!CSRFValidator.validateCSRFToken(request)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            redirectAttributes.addFlashAttribute("errorMessage", "CSRF token invalid");
            return "redirect:/add";
        }

        Integer userId = (Integer) session.getAttribute("user");
        if (userId == null) {
            return "redirect:/login";
        }
        Optional<UserDTO> userOp = userManager.findById(userId);
        if (userOp.isEmpty()) {
            return "redirect:/login";
        }
        if (!userManager.isAdmin(userOp.get())) {
            return "redirect:/login";
        }
        try {
            gameManager.saveGame(newGame, imageFile);
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/add";
        }
        CSRFManager.regenerateCSRFToken(request);
        return "redirect:/NGames";
    }


    @PostMapping("/delete/{id}")
    @Transactional
    public String deleteGame(@PathVariable int id, HttpSession session, Model model, RedirectAttributes redirectAttributes,
                             HttpServletRequest request, HttpServletResponse response) {
        if (!CSRFValidator.validateCSRFToken(request)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            redirectAttributes.addFlashAttribute("errorMessage", "CSRF token invalid");
            return "redirect:/NGames";
        }
        Optional<UserDTO> userop = userManager.findById((Integer) session.getAttribute("user"));
        if (userop.isEmpty()) {
            return "redirect:/login";
        }
        UserDTO user = userop.get();
        if (userManager.isAdmin(user)) {
            try {
                gameManager.deleteGame(id);
                CSRFManager.regenerateCSRFToken(request);
                return "redirect:/NGames";
            } catch (Exception e) {
                redirectAttributes.addFlashAttribute("errorMessage", "Error al borrar el juego: " + e.getMessage());
                return "redirect:/NGames";
            }
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "No se puede borrar el juego");
            return "redirect:/NGames";
        }
    }

    @PostMapping("/user/favourites/add/{id}")
    public String addGameFav(@PathVariable int id, Model model, HttpSession session, RedirectAttributes redirectAttributes,
                             HttpServletRequest request, HttpServletResponse response) {
        if (!CSRFValidator.validateCSRFToken(request)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            redirectAttributes.addFlashAttribute("errorMessage", "CSRF token invalid");
            return "redirect:/game/" + id;
        }
        Integer userId = (Integer) session.getAttribute("user");
        if (userId == null) {
            return "redirect:/login";
        }
        Optional<UserDTO> userOp = userManager.findById(userId);
        if (userOp.isEmpty()) {
            return "redirect:/login";
        }
        UserDTO user = userOp.get();
        Optional<GameDTO> gameOp = gameManager.getGameById(id);
        if (gameOp.isEmpty()) {
            return "redirect:/NGames";
        }
        GameDTO game = gameOp.get();
        try {
            userManager.setFav(user, game);
            model.addAttribute("user", user);
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        CSRFManager.regenerateCSRFToken(request);
        return "redirect:/game/" + id;
    }

    @Transactional
    @PostMapping("/user/favourites/remove/{id}")
    public String removeFavoriteGame(@PathVariable int id, HttpSession session, Model model, RedirectAttributes redirectAttributes,
                                     HttpServletRequest request, HttpServletResponse response) {
        if (!CSRFValidator.validateCSRFToken(request)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            redirectAttributes.addFlashAttribute("errorMessage", "CSRF token invalid");
            return "redirect:/game/" + id;
        }
        Integer userId = (Integer) session.getAttribute("user");
        if (userId == null) {
            return "redirect:/login";
        }
        Optional<UserDTO> userOp = userManager.findById(userId);
        if (userOp.isEmpty()) {
            return "redirect:/login";
        }
        UserDTO user = userOp.get();

        Optional<GameDTO> gameOp = gameManager.getGameById(id);
        if (gameOp.isEmpty()) {
            return "redirect:/NGames";
        }
        GameDTO game = gameOp.get();

        try {
            userManager.deleteFav(user, game);
            model.addAttribute("user", user);
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        CSRFManager.regenerateCSRFToken(request);
        return "redirect:/game/" + id;
    }

    @Transactional
    @GetMapping("/game/{id}")
    public String showGameDetails(@PathVariable int id, Model model, HttpSession session, RedirectAttributes redirectAttributes,
                                  HttpServletRequest request) {
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
        UserDTO user = userOp.get();
        model.addAttribute("user", user);

        Optional<GameDTO> op = gameManager.getGameById(id);
        if (op.isPresent()) {
            GameDTO game = op.get();

            model.addAttribute("game", game);
            model.addAttribute("isFavorite", gameManager.isLiked(game, user));

            return "game-details";
        } else {
            redirectAttributes.addFlashAttribute("error", "El juego seleccionado no existe");
            return "redirect:/NGames";
        }
    }


    // To download and access images, adapted BLOP typefile
    @GetMapping("/game/{id}/image")
    public ResponseEntity<Object> downloadImage(@PathVariable int id) throws SQLException {

        Optional<GameDTO> op = gameManager.getGameById(id);

        if (op.isPresent() && gameMapper.toEntity(op.get()).getImage() != null) {
            Blob image = gameMapper.toEntity(op.get()).getImage();
            Resource file = new InputStreamResource(image.getBinaryStream());

            return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, "image/jpeg").contentLength(image.length()).body(file);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
