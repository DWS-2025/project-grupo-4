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

    //Done
    public PrizeController(PrizeManager prizeManager) {
        this.prizeManager = prizeManager;
    }
    @PostConstruct
    public void init() {
        prizeManager.postConstruct();
    }
    //Done
    @GetMapping("/prizes")
    public String showPrizes(Model model, HttpSession session) {
        model.addAttribute("prizes", prizeManager.findAllPrizes());
        User user = (User) session.getAttribute("user");

        if (user == null) {
            return "prizes";
        }
        model.addAttribute("user", user);
        return "staticLoggedIn/loggedPrizes";
    }
    //Done
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
        prizeManager.save(newPrize);
        return "redirect:/prizes";
    }

    @PostMapping("/deletePrize/{id}")
    public String deletePrize(@PathVariable int id, Model model) {
        Optional<Prize> op = prizeManager.findPrizeById(id);
        if(op.isPresent()){
            prizeManager.deletePrize(op.get());
            model.addAttribute("prizes", prizeManager.findAllPrizes());
        }
        return "redirect:/prizes";
    }

    @GetMapping("/editPrize/{id}")
    public String editPrize(Model model, @PathVariable int id) {
        Optional <Prize> editado = prizeManager.findPrizeById(id);
        if (editado.isPresent()) {
            model.addAttribute("prize", editado.get());
            return "staticLoggedIn/editPrizeForm";
        } else {
            return "redirect:/prizes";
        }
    }

    @PostMapping("/updatePrize/{id}")
    public String updatePrize(@ModelAttribute("prize") Prize updatedPrize, @PathVariable int id,
                              @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
                              Model model) throws IOException {

        if (imageFile != null && !imageFile.isEmpty()) {
            String fileName = UUID.randomUUID().toString() + "-" +
                    Paths.get(imageFile.getOriginalFilename()).getFileName().toString();

            Path uploadPath = Paths.get("uploads/");
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            try (InputStream inputStream = imageFile.getInputStream()) {
                Path filePath = uploadPath.resolve(fileName);
                Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
                updatedPrize.setImage("/uploads/" + fileName);
            }
        }

        prizeManager.updatePrizeDetails(updatedPrize, id);
        model.addAttribute("prizes", prizeManager.findAllPrizes());
        return "redirect:/prizes";
    }

    @PostMapping("/buy/{id}")
    public String buyPrize(@PathVariable int id, Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        Optional<Prize> op = prizeManager.findPrizeById(id);
        if (op.isPresent()) {
            Prize prizeBought = op.get();
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
        } else {
            return "redirect:/prizes";
        }
    }
}