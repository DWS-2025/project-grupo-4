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
        Integer userId = (Integer) session.getAttribute("user");
        if (userId == null) {
            return "redirect:/login";
        }
        Optional<UserDTO> userOp = userManager.findById(userId);
        if (userOp.isEmpty()) {
            return "redirect:/login";
        }
        UserDTO user = userOp.get();
        // Case user has no money
        if (amout < 0) {
            redirectAttributes.addFlashAttribute("errorMessage", "Prohibido apostar en negativo");
            return "redirect:/game/" + id;
        }
        Optional<GameDTO> op = gameManager.getGameById(id);
        if (op.isPresent()) {
            GameDTO gamePlayed = op.get();
            try {
                BetDTO bet = betManager.playBet(gamePlayed, user, amout);
                if (bet == null) {
                    redirectAttributes.addFlashAttribute("errorMessage", "Error creando la apuesta");
                    return "redirect:/game/" + id;
                }
                // Update user in session
                redirectAttributes.addFlashAttribute("status", betManager.getStatusDTO(bet));

            } catch (IllegalArgumentException e) {
                redirectAttributes.addFlashAttribute("errorMessage", "Ha ocurrido un error: " + e.getMessage());
                return "redirect:/game/" + id;
            }
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Juego no encontrado");
            return "redirect:/NGames";
        }

        return "redirect:/game/" + id;
    }


    @PutMapping("/deleteBet/{id}")
    public String deleteBet(@PathVariable long id, HttpSession session, RedirectAttributes redirectAttributes) {
        Integer userId = (Integer) session.getAttribute("user");
        if (userId == null) {
            return "redirect:/login";
        }
        Optional<UserDTO> userOp = userManager.findById(userId);
        if (userOp.isEmpty()) {
            return "redirect:/login";
        }
        Optional<BetDTO> bet = betManager.findById(id);
        if (bet.isPresent()) {
           try{
               betManager.deleteBet(bet.get(),userOp.get());
           } catch (IllegalArgumentException e) {
               redirectAttributes.addFlashAttribute("errorMessage", "Ha ocurrido un error: " + e.getMessage());
           }
        }
        return "redirect:/user";
    }
}
