package com.casino.grupo4_dws.casinoweb.controllers;

import com.casino.grupo4_dws.casinoweb.managers.UserManager;
import com.casino.grupo4_dws.casinoweb.model.Prize;
import com.casino.grupo4_dws.casinoweb.model.User;
import com.casino.grupo4_dws.casinoweb.managers.PrizeManager;
import com.casino.grupo4_dws.casinoweb.repos.PrizeRepository;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.multipart.MultipartFile;
import javax.sql.rowset.serial.SerialBlob;
import java.io.IOException;
import java.sql.Blob;
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
    private UserManager userManager;

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
    public String addGame(@ModelAttribute("newPrize") Prize newPrize,
                          @RequestParam("imageFile") MultipartFile imageFile) throws IOException {
        if (!imageFile.isEmpty()) {
            try (InputStream inputStream = imageFile.getInputStream()) {
                byte[] bytes = inputStream.readAllBytes();
                Blob imageBlob = new SerialBlob(bytes);
                newPrize.setImage(imageBlob);
            } catch (Exception e) {
                throw new RuntimeException("Error al procesar la imagen", e);
            }
        }

        prizeManager.save(newPrize, imageFile);
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
    public String updatePrize(@ModelAttribute("prize") Prize updatedPrize,
                              @PathVariable int id,
                              @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
                              Model model) throws IOException {

        if (imageFile != null && !imageFile.isEmpty()) {
            try (InputStream inputStream = imageFile.getInputStream()) {
                byte[] bytes = inputStream.readAllBytes();
                Blob imageBlob = new javax.sql.rowset.serial.SerialBlob(bytes);
                updatedPrize.setImage(imageBlob); // Ahora es un Blob
            } catch (Exception e) {
                throw new RuntimeException("Error al procesar la imagen", e);
            }
        }

        prizeManager.updatePrizeDetails(updatedPrize, id);
        model.addAttribute("prizes", prizeManager.findAllPrizes());
        return "redirect:/prizes";
    }



    @PostMapping("/buy/{id}")
    public String buyPrize(@PathVariable int id, Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Debes iniciar sesión para comprar un premio.");
            return "redirect:/login";
        }

        Optional<Prize> op = prizeManager.findPrizeById(id);
        if (!op.isPresent()) {
            redirectAttributes.addFlashAttribute("errorMessage", "El premio no existe.");
            return "redirect:/prizes";
        }

        Prize prize = op.get();
        try {
            Prize boughtPrize = userManager.buyPrize(prize, user);
            model.addAttribute("user", user);
            redirectAttributes.addFlashAttribute("successMessage", "¡Compra realizada con éxito!");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/prizes";
    }
}