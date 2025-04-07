package com.casino.grupo4_dws.casinoweb.controllers;

import com.casino.grupo4_dws.casinoweb.dto.BetDTO;
import com.casino.grupo4_dws.casinoweb.dto.GameDTO;
import com.casino.grupo4_dws.casinoweb.dto.UserDTO;
import com.casino.grupo4_dws.casinoweb.managers.BetManager;
import com.casino.grupo4_dws.casinoweb.managers.PrizeManager;
import com.casino.grupo4_dws.casinoweb.managers.UserManager;
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

@Controller
public class GamesController {

    @Autowired
    private GameManager gameManager;
    @Autowired
    private UserManager userManager;
    @Autowired
    private BetManager betManager;

    @PostConstruct
    public void init()  {
        try {
            gameManager.postConstruct();
        } catch (IOException | SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/NGames")
    public String showGames(Model model, HttpSession session) {
        model.addAttribute("games",gameManager.getGameList()); // Pasar la lista de juegos a la vista

        UserDTO user = (UserDTO) session.getAttribute("user");
        if (user == null) {
            return "NGames";
        }
        model.addAttribute("user", user);
        return "staticLoggedIn/loggedGames";
    }

    //Adaptada a H2
    @GetMapping("/NGames/mostLiked")
    public String mostLiked(Model model, HttpSession session) {
        List<GameDTO> games = new ArrayList<>(gameManager.getGameList());

        games.sort((g1, g2) -> {
            int size1 = g1.getUsersLiked() != null ? g1.getUsersLiked().size() : 0;
            int size2 = g2.getUsersLiked() != null ? g2.getUsersLiked().size() : 0;
            return Integer.compare(size2, size1);
        });
        model.addAttribute("mostLikedGames", games);
        UserDTO user = (UserDTO) session.getAttribute("user");
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

        UserDTO userSession = (UserDTO) session.getAttribute("user");
        UserDTO user = userManager.findByIdMeta(userSession.getId());

        if (user == null) {
            return "redirect:/login";
        }

        if (user.getFavoriteGames() == null) {
            user.setFavoriteGames(new ArrayList<>());
        }

        // Force initialize the collection
        user.getFavoriteGames().size();

        session.setAttribute("user", user);
        model.addAttribute("user", user);
        model.addAttribute("games", user.getFavoriteGames());

        return "GamesMyLiked";
    }

    @GetMapping("/add")
    public String addGameForm(Model model) {
        model.addAttribute("newGame", new Game());
        return "staticLoggedIn/addGameForm";
    }

    //Adaptada a H2
    @PostMapping("/add")
    public String addGame(@ModelAttribute GameDTO newGame, @RequestParam("imageFile") MultipartFile imageFile, RedirectAttributes redirectAttributes) throws IOException {
        try {
            gameManager.saveGame(newGame, imageFile);
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/add";
        }
        return "redirect:/NGames";
    }

    //Adaptada a H2
    @PostMapping("/delete/{id}")
    @Transactional
    public String deleteGame(@PathVariable int id, HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        try {
            GameDTO game = gameManager.getGame(id);
            if (game != null) {
                // Detach game from bets but keep bet history
                List<BetDTO> bets = betManager.findAll();
                for (BetDTO bet : bets) {
                    bet.setGame(null);
                    betManager.save(bet);
                }

                // Remove from users' liked games
                for (UserDTO user : game.getUsersLiked()) {
                    user.getFavoriteGames().remove(game);
                    userManager.save(user);
                }

                gameManager.deleteGame(id);
            }
            return "redirect:/NGames";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al borrar el juego: " + e.getMessage());
            return "redirect:/NGames";
        }
    }

    @PostMapping("/user/favourites/add/{id}")
    public String addGameFav(@PathVariable int id, Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        UserDTO user = (UserDTO) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        GameDTO game = gameManager.getGame(id);
        if (game == null) {
            return "redirect:/NGames";
        }
        try{
            userManager.setFav(user, game);
            model.addAttribute("user", user);
        } catch (IllegalArgumentException e){
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/game/" + id;
    }

    @Transactional
    @PostMapping("/user/favourites/remove/{id}")
    public String removeFavoriteGame(@PathVariable int id, HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        UserDTO userSession = (UserDTO) session.getAttribute("user");
        if (userSession == null) {
            return "redirect:/login";
        }

        // Get fresh instances from database
        UserDTO user = userManager.findByIdMeta(userSession.getId());
        GameDTO game = gameManager.getGame(id);

        if (user == null) {
            return "redirect:/login";
        }
        if (game == null) {
            return "redirect:/NGames";
        }

        try {
            userManager.deleteFav(user, game);
            session.setAttribute("user", user);
            model.addAttribute("user", user);
        } catch (IllegalArgumentException e){
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/game/" + id;
    }

    //Adaptado a H2
    @Transactional
    @GetMapping("/game/{id}")
    public String showGameDetails(@PathVariable int id, Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        User userSession = (User) session.getAttribute("user");
        if (userSession == null) {
            return "redirect:/login";
        }
        UserDTO user = userManager.findByIdMeta(userSession.getId());
        if (user == null) {
            return "redirect:/login";
        }
        user.getFavoriteGames().size();
        session.setAttribute("user", user);
        model.addAttribute("user", user);

        Optional<GameDTO> op = gameManager.getGameById(id);
        if (op.isPresent()) {
            GameDTO game = op.get();
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

        Optional<GameDTO> op = gameManager.getGameById(id);

        if (op.isPresent() && op.get().getImage() != null) {
            Blob image = op.get().getImage();
            Resource file =  new InputStreamResource(image.getBinaryStream());

            return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, "image/jpeg").contentLength(image.length()).body(file);
        }else{
            return ResponseEntity.notFound().build();
        }
    }
}
