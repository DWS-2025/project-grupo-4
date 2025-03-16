package com.casino.grupo4_dws.casinoweb.controllers;

import com.casino.grupo4_dws.casinoweb.model.Prize;
import com.casino.grupo4_dws.casinoweb.model.User;
import com.casino.grupo4_dws.casinoweb.managers.PrizeManager;
import com.casino.grupo4_dws.casinoweb.repos.PrizeRepository;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Controller
public class PrizeController {
    @Autowired
    private PrizeManager prizeManager;
    @Autowired
    private PrizeRepository prizeRepo;

    public PrizeController(PrizeManager prizeManager) {
        this.prizeManager = prizeManager;
    }
    @PostConstruct
    public void init() {
        prizeRepo.save(new Prize("AWP Dragon Lore", 1500, "AWP Dragon Lore Souvenir FN", "/images/awp_lore.png"));
        prizeRepo.save(new Prize("Viaje Deluxe",3500,"Viaje deluxe a un pueblo perdido de la mano de dios por ahi para dos personas", "/images/albacete.jpg"));
    }
    @GetMapping("/prizes")
    public String showPrizes(Model model, HttpSession session) {
        model.addAttribute("prizes", prizeRepo.findAll());
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
    public String addGame(@ModelAttribute("newPrize") Prize newPrize, @RequestParam("imageFile") MultipartFile imageFile) throws IOException {
        String fileName = UUID.randomUUID().toString() + "-" + imageFile.getOriginalFilename();
        Path uploadPath = Paths.get("src/main/resources/static/images");
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        try (InputStream inputStream = imageFile.getInputStream()) {
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
            newPrize.setImage("/images/" + fileName);
        }
        prizeRepo.save(newPrize);
        return "redirect:/prizes";
    }

    @PostMapping("/deletePrize/{id}")
    public String deletePrize(@PathVariable int id, Model model) {
        Optional<Prize> op = prizeRepo.findPrizeById(id);
        if(op.isPresent()){
            prizeRepo.delete(op.get());
            model.addAttribute("prizes", prizeRepo.findAll());
        }
        return "redirect:/prizes";
    }

    @GetMapping("/editPrize/{id}")
    public String editPrize(Model model, @PathVariable int id) {
        Prize editado = prizeManager.getPrize(id);
        model.addAttribute("prize", editado);
        return "staticLoggedIn/editPrizeForm";
    }

    @PostMapping("/updatePrize/{id}")
    public String updatePrize(@ModelAttribute("prize") Prize updatedPrize, @PathVariable int id, @RequestParam("imageFile") MultipartFile imageFile) throws IOException {
        if (imageFile.isEmpty()) {
            prizeManager.updatePrizeWOImage(updatedPrize, id);
            return "redirect:/prizes";
        }
        String fileName = UUID.randomUUID().toString() + "-" + imageFile.getOriginalFilename();
        Path uploadPath = Paths.get("src/main/resources/static/images");
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        try (InputStream inputStream = imageFile.getInputStream()) {
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
            updatedPrize.setImage("/images/" + fileName);
        }
        prizeManager.updatePrizeNImage(updatedPrize, id);
        return "redirect:/prizes";
    }

    @PostMapping("/buy/{id}")
    public String buyPrize(@PathVariable int id, Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        Prize prizeBought = prizeManager.getPrize(id);
        if (prizeBought == null) {
            return "redirect:/prizes";
        }
        if (prizeBought.getPrice() > user.getMoney()) {
            return "redirect:/prizes";
        }
        user.setMoney(user.getMoney() - prizeBought.getPrice());

        if (user.getInventory() == null) {
            user.setInventory(new ArrayList<>());
        }
        user.getInventory().add(prizeBought);
        model.addAttribute("user", user);

        return "redirect:/prizes";
    }
}