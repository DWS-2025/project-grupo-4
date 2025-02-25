package com.casino.grupo4_dws.casinoweb.controllers;

import com.casino.grupo4_dws.casinoweb.model.Game;
import org.springframework.stereotype.Controller;
import com.casino.grupo4_dws.casinoweb.model.User;
import com.casino.grupo4_dws.casinoweb.services.GameManager;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
}
