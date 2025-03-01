package com.casino.grupo4_dws.casinoweb.controllers;

import com.casino.grupo4_dws.casinoweb.model.Game;
import com.casino.grupo4_dws.casinoweb.model.User;
import com.casino.grupo4_dws.casinoweb.services.GameManager; // Inyectar GameManager
import com.casino.grupo4_dws.casinoweb.services.UserManager;  // Inyectar UserManager
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
public class SessionController {

    @Autowired
    private UserManager userManager;  // Inyectar UserManager

    @Autowired
    private GameManager gameManager;  // Inyectar GameManager para obtener los juegos disponibles

    @GetMapping("/login")
    public String loadLoginPage() {
        return "login";
    }

    @PostMapping("/login")
    public String loginUser(Model model, @RequestParam String loginUsername, @RequestParam String loginPassword, HttpSession session) {
        if (loginUsername == null || loginUsername.trim().isEmpty()) {
            return "redirect:/login";
        }

        User user = new User();
        user.setUserName(loginUsername);
        user.setPassword(loginPassword);
        user.setMoney(5000);
        user.setInventario(null);
        if ("admin".equals(loginUsername) && "admin".equals(loginPassword)) {
            user.setIsadmin(true);
        }

        session.setAttribute("user", user);
        return "redirect:/";
    }

    @GetMapping("/logout")
    public String loggingOut(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user != null) {
            user.setIsadmin(false);
            user.setUserName("");
            user.setPassword("");
            user.setMoney(0);
            user.setInventario(null);
            session.removeAttribute("user");
        }
        return "redirect:/logoutConfirmar";
    }

    @GetMapping("/logoutConfirmar")
    public String logoutConfirmar(HttpSession session) {
        return "redirect:/";
    }

    @GetMapping("/register")
    public String registerUser() {
        return "register";
    }

    // Agregar juego a favoritos
    @PostMapping("/user/favourites/add/{id}")
    public String addFavoriteGame(@RequestParam int gameId, HttpSession session) {
        User user = (User) session.getAttribute("user");
        Game game = gameManager.getGame(gameId);


        if (user.getGamesLiked() == null) {
            user.setGamesLiked(new ArrayList<>());
        }
        if(game.getUsersLiked() == null){
            game.setUsersLiked(new ArrayList<>());
        }
            if (game != null) {
                userManager.addFavoriteGame(user, game);
                game.getUsersLiked().add(user);
            }

            return "redirect:/favourites";
        }


    // Eliminar juego de favoritos
    @PostMapping("/user/favorites/remove")
    public String removeFavoriteGame(@RequestParam int gameId, HttpSession session) {
        User user = (User) session.getAttribute("user");

        // Obtener el juego de la lista de juegos disponibles usando gameManager
        List<Game> availableGames = gameManager.getGameList();
        Game gameToRemove = availableGames.stream()
                .filter(game -> game.getId() == gameId)
                .findFirst()
                .orElse(null);

        if (gameToRemove != null) {
            userManager.removeFavoriteGame(user, gameToRemove);
        }

        return "redirect:/favorites";
    }

    // Mostrar juegos favoritos
    @GetMapping("/favorites")
    public String showFavorites(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        model.addAttribute("favoriteGames", userManager.getFavoriteGames(user));
        return "favourites"; // Vista donde se muestran los juegos favoritos
    }

    // Mostrar lista de juegos disponibles
    @GetMapping("/session/NJuegos")
    public String mostrarJuegos(Model model, HttpSession session) {
        List<Game> games = gameManager.getGameList();  // Obtener la lista de juegos
        model.addAttribute("games", games);  // Pasar la lista de juegos a la vista

        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "NJuegos"; // Si no hay usuario logueado, redirigir a la página de juegos
        }
        model.addAttribute("user", user);
        return "staticLoggedIn/loggedGames";  // Mostrar la vista de juegos si el usuario está logueado
    }
}
