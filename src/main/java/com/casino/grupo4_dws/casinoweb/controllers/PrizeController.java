package com.casino.grupo4_dws.casinoweb.controllers;

import com.casino.grupo4_dws.casinoweb.dto.PrizeDTO;
import com.casino.grupo4_dws.casinoweb.dto.UserDTO;
import com.casino.grupo4_dws.casinoweb.managers.UserManager;
import com.casino.grupo4_dws.casinoweb.mapper.PrizeMapper;
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
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Controller
public class PrizeController {
    @Autowired
    private PrizeManager prizeManager;
    @Autowired
    private UserManager userManager;
    @Autowired
    private PrizeMapper prizeMapper;

    public PrizeController(PrizeManager prizeManager) {
        this.prizeManager = prizeManager;
    }

    @PostConstruct
    public void init() throws SQLException, IOException {
        prizeManager.postConstruct();
    }

    //Done
    @GetMapping("/prizes")
    public String showPrizes(Model model, HttpSession session) {
        model.addAttribute("prizes", prizeManager.findAllPrizes());
        Integer userId = (Integer) session.getAttribute("user");
        if (userId == null) {
            return "prizes";
        }
        Optional<UserDTO> userOp = userManager.findById(userId);
        if (userOp.isEmpty()) {
            return "prizes";
        }
        model.addAttribute("user", userOp.get());
        return "staticLoggedIn/loggedPrizes";
    }

    @PostMapping("/filterPrizes")
    public String filterPrizes(Model model, HttpSession session, @RequestParam(required = false) String title,
                               @RequestParam(required = false, defaultValue = "0") Integer minPrice,
                               @RequestParam(required = false, defaultValue = "999999") Integer maxPrice) {

        model.addAttribute("prizes", prizeManager.findPrizesByFilters(title, minPrice, maxPrice));
        Integer userId = (Integer) session.getAttribute("user");
        if (userId == null) {
            return "prizes";
        }
        Optional<UserDTO> userOp = userManager.findById(userId);
        if (userOp.isEmpty()) {
            return "prizes";
        }
        model.addAttribute("user", userOp.get());
        return "staticLoggedIn/loggedPrizes";
    }

    @GetMapping("/addPrize")
    public String addGameForm(Model model) {
        model.addAttribute("newPrize", new Prize());
        return "staticLoggedIn/addPrizeForm";
    }

    @PostMapping("/addPrize")
    public String addPrize(@ModelAttribute("newPrize") Prize newPrize, @RequestParam("imageFile") MultipartFile imageFile) throws IOException, SQLException {
        prizeManager.savePrize(newPrize, imageFile);
        return "redirect:/prizes";
    }

    @PostMapping("/deletePrize/{id}")
    public String deletePrize(@PathVariable int id, Model model) {
        Optional<PrizeDTO> op = prizeManager.getPrizeById(id);
        if (op.isPresent()) {
            prizeManager.deletePrize(id);
            model.addAttribute("prizes", prizeManager.findAllPrizes());
        }
        return "redirect:/prizes";
    }

    @GetMapping("/editPrize/{id}")
    public String editPrize(Model model, @PathVariable int id) {
        Optional<PrizeDTO> editado = prizeManager.getPrizeById(id);
        if (editado.isPresent()) {
            model.addAttribute("prize", editado.get());
            return "staticLoggedIn/editPrizeForm";
        } else {
            return "redirect:/prizes";
        }
    }

    @PostMapping("/updatePrize/{id}")
    public String updatePrize(@ModelAttribute("prize") PrizeDTO updatedPrize, @PathVariable int id,
                              @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
                              Model model) throws IOException {

        prizeManager.updatePrizeDetails(updatedPrize, id, imageFile);
        model.addAttribute("prizes", prizeManager.findAllPrizes());
        return "redirect:/prizes";
    }


    @PostMapping("/buy/{id}")
    public String buyPrize(@PathVariable int id, Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        Integer userId = (Integer) session.getAttribute("user");
        if (userId == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Debes iniciar sesión para comprar un premio.");
            return "redirect:/login";
        }
        Optional<UserDTO> userOp = userManager.findById(userId);
        if (userOp.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Debes iniciar sesión para comprar un premio.");
            return "redirect:/login";
        }
        UserDTO userdto = userOp.get();
        Optional<PrizeDTO> op = prizeManager.getPrizeById(id);
        if (!op.isPresent()) {
            redirectAttributes.addFlashAttribute("errorMessage", "El premio no existe.");
            return "redirect:/prizes";
        }

        PrizeDTO prize = op.get();
        try {
            userManager.buyPrize(prize, userdto);
            redirectAttributes.addFlashAttribute("successMessage", "¡Compra realizada con éxito!");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/prizes";
    }

    @GetMapping("/prize/{id}/image")
    public ResponseEntity<Object> downloadImage(@PathVariable int id) throws SQLException {

        Optional<PrizeDTO> op = prizeManager.getPrizeById(id);

        if (op.isPresent() && prizeMapper.toEntity(op.get()).getImage() != null) {
            Blob image = prizeMapper.toEntity(op.get()).getImage();
            Resource file = new InputStreamResource(image.getBinaryStream());

            return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, "image/jpeg").contentLength(image.length()).body(file);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}