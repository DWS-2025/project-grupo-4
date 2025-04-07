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

        // Case there's no user
        if (user == null) {
            return "redirect:/login";
        }

        // Case user has no money
        if (amout < 0) {
            redirectAttributes.addFlashAttribute("errorMessage", "Prohibido apostar en negativo");
            return "redirect:/game/" + id;
        }

        Optional<GameDTO> op = gameManager.getGameById(id);
        if (op.isPresent()) {
            GameDTO gamePlayed = op.get();
            try {
                // First save the user to ensure it exists in the database
                userManager.save(user);

                BetDTO bet = betManager.playBet(gamePlayed, user, amout);

                if (bet == null) {
                    redirectAttributes.addFlashAttribute("errorMessage", "Error creando la apuesta");
                    return "redirect:/game/" + id;
                }

                // Asegúrate de que el usuario esté seteado en la apuesta
                bet.setUserPlayer(user); // <-- Esta es la línea clave

                // Guardar la apuesta
                betManager.save(bet);

                // Update user in session
                session.setAttribute("user", user);
                redirectAttributes.addFlashAttribute("user", user);
                redirectAttributes.addFlashAttribute("status", bet.isStatus());

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
