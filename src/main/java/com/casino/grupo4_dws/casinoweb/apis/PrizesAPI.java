package com.casino.grupo4_dws.casinoweb.apis;

import com.casino.grupo4_dws.casinoweb.managers.PrizeManager;
import com.casino.grupo4_dws.casinoweb.managers.UserManager;
import com.casino.grupo4_dws.casinoweb.model.Prize;
import com.casino.grupo4_dws.casinoweb.model.User;
import com.casino.grupo4_dws.casinoweb.dto.PrizeDTO;
import com.casino.grupo4_dws.casinoweb.mapper.PrizeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.hibernate.engine.jdbc.BlobProxy;
import org.springframework.web.bind.annotation.RequestPart;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import org.hibernate.engine.jdbc.BlobProxy;
import org.springframework.http.MediaType;
@RestController
@RequestMapping("/api/prizes")
public class PrizesAPI {

    @Autowired
    private PrizeManager prizeManager;

    @Autowired
    private UserManager userManager;

    @Autowired
    private PrizeMapper prizeMapper;

    // Get all prizes
    @GetMapping("")
    public ResponseEntity<List<PrizeDTO>> getAllPrizes() {
        List<Prize> prizes = prizeManager.findAllPrizes();
        return ResponseEntity.ok(prizeMapper.toDTOList(prizes));
    }

    // Get prize by id
    @GetMapping("/{id}")
    public ResponseEntity<PrizeDTO> getPrize(@PathVariable int id) {
        Optional<Prize> prize = prizeManager.getPrizeById(id);
        return prize.map(p -> ResponseEntity.ok(prizeMapper.toDTO(p)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("")
    public ResponseEntity<PrizeDTO> createPrize(@RequestBody PrizeDTO prizeDTO) {
        try {
            Prize prize = prizeMapper.toEntity(prizeDTO);
            prizeManager.savePrize(prize, null);
            return ResponseEntity.status(HttpStatus.CREATED).body(prizeMapper.toDTO(prize));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    // Update prize
    @PutMapping("/{id}")
    public ResponseEntity<PrizeDTO> updatePrize(@PathVariable int id, @RequestBody PrizeDTO prizeDTO) {
        try {
            Prize prize = prizeMapper.toEntity(prizeDTO);
            prizeManager.updatePrizeDetails(prize, id);
            return ResponseEntity.ok(prizeMapper.toDTO(prize));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Delete prize
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePrize(@PathVariable int id) {
        Optional<Prize> prize = prizeManager.getPrizeById(id);
        if (prize.isPresent()) {
            prizeManager.deletePrize(prize.get());
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    // Buy prize
    @PostMapping("/{id}/buy")
    public ResponseEntity<?> buyPrize(@PathVariable int id, @RequestBody User user) {
        Optional<Prize> prizeOpt = prizeManager.getPrizeById(id);
        if (!prizeOpt.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        try {
            Prize boughtPrize = userManager.buyPrize(prizeOpt.get(), user);
            return ResponseEntity.ok(boughtPrize);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}