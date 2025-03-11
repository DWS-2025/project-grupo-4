package com.casino.grupo4_dws.casinoweb.controllers;

import com.casino.grupo4_dws.casinoweb.model.Game;
import org.springframework.stereotype.Controller;
import com.casino.grupo4_dws.casinoweb.model.User;
import com.casino.grupo4_dws.casinoweb.services.GameManager;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import java.util.ArrayList;
import java.util.List;

@Controller
public class GamesController {

    @Autowired
    private GameManager gameManager;

    public GamesController(GameManager gameManager) {
        this.gameManager = gameManager;
    }


    @GetMapping("/NGames")
    public String showGames(Model model, HttpSession session) {
        List<Game> games = gameManager.getGameList();
        model.addAttribute("games", games); // Pasar la lista de juegos a la vista

        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "NGames";
        }
        model.addAttribute("user", user);
        return "staticLoggedIn/loggedGames";
    }

    @GetMapping("/NGames/mostLiked")
    public String mostLiked(Model model, HttpSession session) {
        List<Game> games = new ArrayList<>(gameManager.getGameList());

        games.sort((g1, g2) -> {
            int size1 = g1.getUsersLiked() != null ? g1.getUsersLiked().size() : 0;
            int size2 = g2.getUsersLiked() != null ? g2.getUsersLiked().size() : 0;
            return Integer.compare(size2, size1);
        });
        model.addAttribute("mostLikedGames", games);
        User user = (User) session.getAttribute("user");
        model.addAttribute("user", user);
        return "GamesMostLiked";
    }

    @GetMapping("/NGames/liked")
    public String liked(Model model, HttpSession session) {
        if (session.getAttribute("user") == null) {
            return "redirect:/login";
        }
        User user = (User) session.getAttribute("user");
        model.addAttribute("user", user);
        if (user.getGamesLiked() == null) {
            user.setGamesLiked(new ArrayList<>());
        }
        List<Game> games = user.getGamesLiked();
        model.addAttribute("games", games);
        return "GamesMyLiked";
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

        return "redirect:/NGames";
    }

    @PostMapping("/delete/{id}")
    public String deleteGame(@PathVariable int id, HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user.getGamesLiked() == null) {
            user.setGamesLiked(new ArrayList<>());
        }
        if (user.getGamesLiked().contains(gameManager.getGame(id))) {
            user.getGamesLiked().remove(gameManager.getGame(id));
        }
        if (gameManager.getGame(id) != null) {
            gameManager.removeGameId(id);
        }
        return "redirect:/NGames"; // Redirigir a la lista de juegos
    }

    @PostMapping("/user/favourites/add/{id}")
    public String addGameFav(@PathVariable int id, Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        Game game = gameManager.getGame(id);
        if (game == null) {
            return "redirect:/NGames";
        }
        if (game.getUsersLiked() == null) {
            game.setUsersLiked(new ArrayList<>());
        }
        if (user.getGamesLiked() == null) {
            user.setGamesLiked(new ArrayList<>());
        }
        if (user.getGamesLiked().contains(game)) {
            return "redirect:/game/" + id;
        }
        user.getGamesLiked().add(game);
        game.getUsersLiked().add(user);


        model.addAttribute("user", user);
        return "redirect:/game/" + id;
    }

    @PostMapping("/user/favourites/remove/{id}")
    public String removeFavoriteGame(@RequestParam int gameId, HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        Game game = gameManager.getGame(gameId);
        if (user == null) {
            return "redirect:/login";
        }
        if (game == null) {
            return "redirect:/NGames";
        }
        user.getGamesLiked().remove(game);
        game.getUsersLiked().remove(user);
        model.addAttribute("user", user);

        return "redirect:/game/" + gameId;
    }

    @GetMapping("/game/{id}")
    public String showGameDetails(@PathVariable int id, Model model, HttpSession session) {
        Game game = gameManager.getGame(id);
        if (game == null) {
            return "redirect:/NGames";
        }
        if (game.getUsersLiked() == null) {
            game.setUsersLiked(new ArrayList<>());
        }

        User user = (User) session.getAttribute("user");
        if (user != null) {
            model.addAttribute("user", user);
        }
        if (user.getGamesLiked() == null) {
            user.setGamesLiked(new ArrayList<>());
        }

        model.addAttribute("game", game);
        model.addAttribute("user", user);
        return "game-details";
    }
}
