package com.casino.grupo4_dws.casinoweb.controllers;

import com.casino.grupo4_dws.casinoweb.model.Game;
import com.casino.grupo4_dws.casinoweb.model.Prize;
import com.casino.grupo4_dws.casinoweb.model.User;
import com.casino.grupo4_dws.casinoweb.services.GameManager;
import com.casino.grupo4_dws.casinoweb.services.PrizeManager;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class prizeController {
    @Autowired
    private final PrizeManager prizeManager;
    public prizeController(PrizeManager prizeManager) {
        this.prizeManager = prizeManager;
    }

    @Autowired
    private User user;

    @GetMapping("/prizes")
    public String showPrizes(Model model, HttpSession session) {
        List <Prize> prizes = prizeManager.getPrizeList();
        model.addAttribute("prizes", prizes);
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "prizes";
        }
        model.addAttribute("user", user);
        return "staticLoggedIn/loggedPrizes";
    }

    @GetMapping("/addPrize")
    public String addGameForm(Model model) {
        model.addAttribute("newPrize", new Prize());
        return "staticLoggedIn/addPrizeForm";
    }
    @PostMapping("/addPrize")
    public String addGame(@ModelAttribute("newPrize") Prize newPrize) {
        prizeManager.addPrize(newPrize);
        return "redirect:/prizes";
    }
    @PostMapping("/deletePrize/{id}")
    public String eliminarJuego(@PathVariable int id) {
        prizeManager.removePrizeId(id);
        return "redirect:/prizes";
    }

//    @PostMapping("/buyPrize/{id}")
//    public String buyPrize(@PathVariable int id) {
//    }

}
