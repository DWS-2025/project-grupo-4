package com.casino.grupo4_dws.casinoweb.controllers;

import com.casino.grupo4_dws.casinoweb.dto.BetDTO;
import com.casino.grupo4_dws.casinoweb.dto.GameDTO;
import com.casino.grupo4_dws.casinoweb.dto.UserDTO;
import com.casino.grupo4_dws.casinoweb.managers.UserManager;
import com.casino.grupo4_dws.casinoweb.model.Bet;
import com.casino.grupo4_dws.casinoweb.model.Game;
import com.casino.grupo4_dws.casinoweb.managers.BetManager;
import org.springframework.stereotype.Controller;
import com.casino.grupo4_dws.casinoweb.model.User;
import com.casino.grupo4_dws.casinoweb.managers.GameManager;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
public class BetController {


    @Autowired
    private BetManager betManager;
    @Autowired
    private GameManager gameManager;
    @Autowired
    private UserManager userManager;

    // ENDPOINT TO PLACE A BET
    @PostMapping("/playGame/{id}")
    public String playGame(@PathVariable int id, Model model, HttpSession session, @RequestParam("playedAmount") int amout, RedirectAttributes redirectAttributes) {
        UserDTO user = (UserDTO) session.getAttribute("user");

        // Case user has no money
        if (amout < 0) {
            redirectAttributes.addFlashAttribute("errorMessage", "Prohibido apostar en negativo");
            return "redirect:/game/" + id;
        }
        // Case there's no user
        if (user == null) {
            return "redirect:/login";
        }

        Optional<GameDTO> op = gameManager.getGameById(id);
        if (op.isPresent()) {
            GameDTO gamePlayed = op.get();
            try {
                BetDTO bet = betManager.playBet(gamePlayed, user, amout);
                redirectAttributes.addFlashAttribute("user", user);
                betManager.save(bet);
                userManager.save(user);
                boolean status = bet.isStatus();
                redirectAttributes.addFlashAttribute("status", status);
            } catch (IllegalArgumentException e) {
                redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
                return "redirect:/game/" + id;
            } catch (Exception e) {
                redirectAttributes.addFlashAttribute("errorMessage", "Ha ocurrido un error: " + e.getMessage());
                return "redirect:/game/" + id;
            }
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Game not found");
            return "redirect:/NGames";
        }


        return "redirect:/game/" + id;
    }

    @PostMapping("/deleteBet/{id}")
    public String deleteBet(@PathVariable long id, HttpSession session, RedirectAttributes redirectAttributes) {
        UserDTO user = (UserDTO) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        Optional<BetDTO> bet = betManager.findById(id);
        if (bet.isPresent() && bet.get().getId() == user.getId()) {
            BetDTO betToHide = bet.get();
            betToHide.setShow(false);
            betManager.save(betToHide);
        }
        return "redirect:/user";
    }
}
