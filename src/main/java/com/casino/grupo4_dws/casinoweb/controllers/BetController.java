package com.casino.grupo4_dws.casinoweb.controllers;

import com.casino.grupo4_dws.casinoweb.model.Bet;
import com.casino.grupo4_dws.casinoweb.model.Game;
import com.casino.grupo4_dws.casinoweb.services.BetManager;
import org.springframework.stereotype.Controller;
import com.casino.grupo4_dws.casinoweb.model.User;
import com.casino.grupo4_dws.casinoweb.services.GameManager;
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
import java.util.UUID;

import java.util.ArrayList;
import java.util.List;

@Controller
public class BetController {


    @Autowired
    private BetManager betManager;
    @Autowired
    private GameManager gameManager;

    // ENDPOINT TO PLACE A BET
    @PostMapping("/playGame/{id}")
    public String playGame(@PathVariable int id, Model model, HttpSession session, @RequestParam("playedAmount") int amout, RedirectAttributes redirectAttributes) {
        Game gamePlayed = gameManager.getGame(id);
        User user = (User) session.getAttribute("user");

        // Case user has no money
        if (amout < 0) {
            redirectAttributes.addFlashAttribute("errorMessage", "Prohibido apostar en negativo");
            return "redirect:/game/" + id;
        }
        // Case there's no user
        if (user == null) {
            return "redirect:/login";
        }
        // Case there's no game value
        if (gamePlayed == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Game not found");
            return "redirect:/login";
        }
        // If bet params are ok, play bet
        try {
            Bet bet = betManager.playBet(gamePlayed, user, amout);
            redirectAttributes.addFlashAttribute("user", user);
            boolean status = bet.GetStatus();
            redirectAttributes.addFlashAttribute("status", status);
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/game/" + id;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Something went wrong");
            return "redirect:/game/" + id;
        }

        return "redirect:/game/" + id;
    }
}
