package com.casino.grupo4_dws.casinoweb.controllers;

import com.casino.grupo4_dws.casinoweb.managers.UserManager;
import com.casino.grupo4_dws.casinoweb.model.Game;
import com.casino.grupo4_dws.casinoweb.model.Prize;
import com.casino.grupo4_dws.casinoweb.model.User;
import com.casino.grupo4_dws.casinoweb.managers.PrizeManager;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpSession;
import org.hibernate.engine.jdbc.BlobProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
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
import java.sql.Blob;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;

@Controller
public class PrizeController {
    @Autowired
    private PrizeManager prizeManager;
    @Autowired
    private UserManager userManager;

    public PrizeController(PrizeManager prizeManager) {
        this.prizeManager = prizeManager;
    }
    @PostConstruct
    public void init() throws SQLException, IOException {
        try {
            prizeManager.postConstruct();
        } catch (IOException | SQLException e) {
            throw new RuntimeException(e);
        }
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
    public String addPrize(@ModelAttribute("newPrize") Prize newPrize, @RequestParam("imageFile") MultipartFile imageFile) throws IOException {

        String fileName = UUID.randomUUID().toString() + "-" + imageFile.getOriginalFilename();
        Path uploadPath = Paths.get("src/main/resources/static/images");
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        try (InputStream inputStream = imageFile.getInputStream()) {
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
            newPrize.setImage(BlobProxy.generateProxy(imageFile.getInputStream(), imageFile.getSize()));
        }

        prizeManager.save(newPrize);
        return "redirect:/prizes";
    }

    @PostMapping("/deletePrize/{id}")
    public String deletePrize(@PathVariable int id, Model model) {
        Optional<Prize> op = prizeManager.getPrizeById(id);
        if(op.isPresent()){
            prizeManager.deletePrize(op.get());
            model.addAttribute("prizes", prizeManager.findAllPrizes());
        }
        return "redirect:/prizes";
    }

    @GetMapping("/editPrize/{id}")
    public String editPrize(Model model, @PathVariable int id) {
        Optional <Prize> editado = prizeManager.getPrizeById(id);
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
            String fileName = UUID.randomUUID().toString() + "-" + imageFile.getOriginalFilename();

            Path uploadPath = Paths.get("src/main/resources/static/images");
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            try (InputStream inputStream = imageFile.getInputStream()) {
                Path filePath = uploadPath.resolve(fileName);
                Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
                updatedPrize.setImage(BlobProxy.generateProxy(imageFile.getInputStream(), imageFile.getSize()));
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

        Optional<Prize> op = prizeManager.getPrizeById(id);
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

    @GetMapping("/prize/{id}/image")
    public ResponseEntity<Object> downloadImage(@PathVariable int id) throws SQLException {

        Optional<Prize> op = prizeManager.getPrizeById(id);

        if (op.isPresent() && op.get().getImage() != null) {
            Blob image = op.get().getImage();
            Resource file =  new InputStreamResource(image.getBinaryStream());

            return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, "image/jpeg").contentLength(image.length()).body(file);
        }else{
            return ResponseEntity.notFound().build();
        }
    }
}