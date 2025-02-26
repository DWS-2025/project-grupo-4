package com.casino.grupo4_dws.casinoweb.controllers;

import com.casino.grupo4_dws.casinoweb.model.Game;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import com.casino.grupo4_dws.casinoweb.model.User;
import com.casino.grupo4_dws.casinoweb.services.GameManager;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Controller
public class gamesController {

@Autowired
private GameManager gameManager;
public gamesController(GameManager gameManager) {
    this.gameManager = gameManager;
}


@Autowired
private User user;

    @GetMapping("/NJuegos")
    public String mostrarJuegos(Model model, HttpSession session) {
        List<Game> games = gameManager.getGameList();
        model.addAttribute("games", games); // Pasar la lista de juegos a la vista

        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "NJuegos";
        }
        model.addAttribute("user", user);
        return "staticLoggedIn/loggedGames";
    }

    @GetMapping("/add")
    public String addGameForm(Model model) {
        model.addAttribute("newGame", new Game());
        return "staticLoggedIn/addGameForm";
    }

    @PostMapping("/add")
    public String addGame(@ModelAttribute Game newGame, @RequestParam("imageFile") MultipartFile imageFile) throws IOException {
        // Generate a unique filename to avoid conflicts
        String fileName = UUID.randomUUID().toString() + "_" + imageFile.getOriginalFilename();

        // Set the path where the image will be saved
        Path uploadPath = Paths.get("src/main/resources/static/images");

        // Create directories if they don't exist
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Save the file
        try (InputStream inputStream = imageFile.getInputStream()) {
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
        }

        // Set the image path in the game object
        newGame.setImage("/images/" + fileName);

        // Save the game to the database
        gameManager.addGame(newGame);

        return "redirect:/NJuegos";
    }

    @PostMapping("/delete/{id}")
    public String eliminarJuego(@PathVariable int id) {
        gameManager.removeGameId(id);
        return "redirect:/NJuegos"; // Redirigir a la lista de juegos
    }

    @PostMapping("gameFav/{id}")
    public void addGameFav(@PathVariable int id, Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return;
        }
        Game game = gameManager.getGame(id);
        if (game == null) {
            return;
        }
        if(game.getUsersLiked() == null){
            game.setUsersLiked(new ArrayList<>());
        }
        if(user.getGamesLiked() == null){
            user.setGamesLiked(new ArrayList<>());
        }
        
    }
}
