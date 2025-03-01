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

    @PostMapping("/playGame/{id}")
    public String playGame(@PathVariable int id, Model model, HttpSession session, @RequestParam("playedAmout") int amout, RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        Game gamePlayed = gameManager.getGame(id);
        if (gamePlayed == null) {
            return "redirect:/login";
        }
        Bet bet = betManager.playBet(gamePlayed,user,amout);
        model.addAttribute("user", user);
        boolean status = bet.GetStatus();
        redirectAttributes.addFlashAttribute("status", status);
        return "redirect:/game/" + id;
    }
}
