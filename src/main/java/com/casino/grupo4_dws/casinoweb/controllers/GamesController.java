package com.casino.grupo4_dws.casinoweb.controllers;

import com.casino.grupo4_dws.casinoweb.managers.UserManager;
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

@Controller
public class GamesController {

    @Autowired
    private GameManager gameManager;
    @Autowired
    private UserManager userManager;

    @PostConstruct
    public void init()  {
        try {
            gameManager.PostConstruct();
        } catch (IOException | SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/NGames")
    public String showGames(Model model, HttpSession session) {
        model.addAttribute("games",gameManager.getGameList()); // Pasar la lista de juegos a la vista

        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "NGames";
        }
        model.addAttribute("user", user);
        return "staticLoggedIn/loggedGames";
    }

    //Adaptada a H2
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
        if (user == null) {
            return "redirect:/NGames";
        }
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

    //Adaptada a H2
    @PostMapping("/add")
    public String addGame(@ModelAttribute Game newGame, @RequestParam("imageFile") MultipartFile imageFile) throws IOException {
        /*
        // Chavales esto es el procesado de las imagenes antes de la base de datos, si funciona bien la bbdd lo quitamos
        // pero lo dejo por si acaso

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
        gameManager.saveGame(newGame);
        */
        gameManager.saveGame(newGame, imageFile);

        return "redirect:/NGames";
    }

    //Adaptada a H2
    @PostMapping("/delete/{id}")
    public String deleteGame(@PathVariable int id, HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute("user");
        if (user.getGamesLiked() == null) {
            user.setGamesLiked(new ArrayList<>());
        }
        try{
            gameManager.deleteGame(id);
            model.addAttribute("games", gameManager.getGameList());
        } catch (IllegalArgumentException e){
            redirectAttributes.addFlashAttribute("error", "El juego a borrar no existe");
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

    //Adaptado a H2
    @Transactional
    @GetMapping("/game/{id}")
    public String showGameDetails(@PathVariable int id, Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        User userSession = (User) session.getAttribute("user");
        if (userSession == null) {
            return "redirect:/login";
        }
        User user = userManager.findByIdMeta(userSession.getId());
        if (user == null) {
            return "redirect:/login";
        }
        user.getGamesLiked().size();
        session.setAttribute("user", user);
        model.addAttribute("user", user);

        Optional<Game> op = gameManager.getGameById(id);
        if (op.isPresent()) {
            Game game = op.get();
            if (game.getUsersLiked() == null) {
                game.setUsersLiked(new ArrayList<>());
            }
            game.getUsersLiked().size();
            model.addAttribute("game", game);
            return "game-details";
        } else {
            redirectAttributes.addFlashAttribute("error", "El juego seleccionado no existe");
            return "redirect:/NGames";
        }





    }


    // To download and access images, adapted to H2 and BLOP typefile
    @GetMapping("/game/{id}/image")
    public ResponseEntity<Object> downloadImage(@PathVariable int id) throws SQLException {

        Optional<Game> op = gameManager.getGameById(id);

        if (op.isPresent() && op.get().getImage() != null) {
            Blob image = op.get().getImage();
            Resource file =  new InputStreamResource(image.getBinaryStream());

            return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, "image/jpeg").contentLength(image.length()).body(file);
        }else{
            return ResponseEntity.notFound().build();
        }
    }
}
