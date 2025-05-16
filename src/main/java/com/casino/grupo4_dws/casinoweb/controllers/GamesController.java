package com.casino.grupo4_dws.casinoweb.controllers;

import com.casino.grupo4_dws.casinoweb.dto.BetDTO;
import com.casino.grupo4_dws.casinoweb.dto.GameDTO;
import com.casino.grupo4_dws.casinoweb.dto.UserDTO;
import com.casino.grupo4_dws.casinoweb.managers.BetManager;
import com.casino.grupo4_dws.casinoweb.managers.PrizeManager;
import com.casino.grupo4_dws.casinoweb.managers.UserManager;
import com.casino.grupo4_dws.casinoweb.mapper.GameMapper;
import com.casino.grupo4_dws.casinoweb.model.Bet;
import com.casino.grupo4_dws.casinoweb.model.Game;
import com.casino.grupo4_dws.casinoweb.repos.GameRepository;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import com.casino.grupo4_dws.casinoweb.model.User;
import com.casino.grupo4_dws.casinoweb.managers.GameManager;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class GamesController {

    @Autowired
    private GameManager gameManager;
    @Autowired
    private GameMapper gameMapper;
    @Autowired
    private UserManager userManager;
    @Autowired
    private BetManager betManager;

    @PostConstruct
    public void init() {
        try {
            gameManager.postConstruct();
        } catch (IOException | SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/NGames")
    public String showGames(Model model, HttpSession session) {
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
    public String mostLiked(Model model, HttpSession session) {
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
    public String liked(Model model, HttpSession session) {
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
    public String addGameForm(Model model, HttpSession session) {
        Integer userId = (Integer) session.getAttribute("user");
        if (userId == null) {
            return "redirect:/login";
        }
        Optional<UserDTO> userOp = userManager.findById(userId);
        if (userOp.isEmpty()) {
            return "redirect:/login";
        }
        if(!userManager.isAdmin(userOp.get())) {
            return "redirect:/login";
        }
        model.addAttribute("newGame", new Game());
        return "staticLoggedIn/addGameForm";
    }


    @PostMapping("/add")
    public String addGame(@ModelAttribute Game newGame, @RequestParam("imageFile") MultipartFile imageFile,
                          RedirectAttributes redirectAttributes, HttpSession session) throws IOException {
        Integer userId = (Integer) session.getAttribute("user");
        if (userId == null) {
            return "redirect:/login";
        }
        Optional<UserDTO> userOp = userManager.findById(userId);
        if (userOp.isEmpty()) {
            return "redirect:/login";
        }
        if(!userManager.isAdmin(userOp.get())) {
            return "redirect:/login";
        }
        try {
            Game sanitizado = gameManager.sanitize(newGame);
            gameManager.saveGame(sanitizado, imageFile);
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/add";
        }
        return "redirect:/NGames";
    }


    @PostMapping("/delete/{id}")
    @Transactional
    public String deleteGame(@PathVariable int id, HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        Optional<UserDTO> userop = userManager.findById((Integer) session.getAttribute("user"));
        if (userop.isEmpty()) {
            return "redirect:/login";
        }
        UserDTO user = userop.get();
        if (userManager.isAdmin(user)) {
            try {
                gameManager.deleteGame(id);
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
    public String addGameFav(@PathVariable int id, Model model, HttpSession session, RedirectAttributes redirectAttributes) {
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
        return "redirect:/game/" + id;
    }

    @Transactional
    @PostMapping("/user/favourites/remove/{id}")
    public String removeFavoriteGame(@PathVariable int id, HttpSession session, Model model, RedirectAttributes redirectAttributes) {
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
        return "redirect:/game/" + id;
    }


    @Transactional
    @GetMapping("/game/{id}")
    public String showGameDetails(@PathVariable int id, Model model, HttpSession session, RedirectAttributes redirectAttributes) {
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
