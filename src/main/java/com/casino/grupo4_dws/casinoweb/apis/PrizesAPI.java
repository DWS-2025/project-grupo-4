package com.casino.grupo4_dws.casinoweb.apis;

import com.casino.grupo4_dws.casinoweb.managers.PrizeManager;
import com.casino.grupo4_dws.casinoweb.managers.UserManager;
import com.casino.grupo4_dws.casinoweb.mapper.UserMapper;
import com.casino.grupo4_dws.casinoweb.model.Prize;
import com.casino.grupo4_dws.casinoweb.model.User;
import com.casino.grupo4_dws.casinoweb.dto.PrizeDTO;
import com.casino.grupo4_dws.casinoweb.dto.UserDTO;
import com.casino.grupo4_dws.casinoweb.mapper.PrizeMapper;
import com.casino.grupo4_dws.casinoweb.repos.PrizeRepository;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
@RestController
@RequestMapping("/api/prizes")
public class PrizesAPI {

    @Autowired
    private PrizeManager prizeManager;

    @Autowired
    private UserManager userManager;

    @Autowired
    private PrizeMapper prizeMapper;

    @Autowired
    private UserMapper userMapper;

    @GetMapping("")
    public ResponseEntity<?> getAllPrizes(Pageable pageable) {
        try {
            Page<Prize> prizePage = prizeManager.findAllPrizes(pageable);
            if (prizePage.isEmpty()) {
                return ResponseEntity.noContent().build();
            }

            Page<PrizeDTO> dtoPage = prizePage.map(prize -> {
                PrizeDTO dto = prizeMapper.toDTO(prize);
                // Prevent serialization issues with Blob/InputStream
                if (dto.getImage() != null) {
                    dto.setImage(null);
                }
                return dto;
            });

            return ResponseEntity.ok(dtoPage);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Error retrieving prizes: " + e.getMessage());
        }
    }

    // Get prize by id
    @GetMapping("/{id}")
    public ResponseEntity<PrizeDTO> getPrize(@PathVariable int id) {
        try {
            Optional<PrizeDTO> prizeOpt = prizeManager.getPrizeById(id);
            if (prizeOpt.isPresent()) {
                PrizeDTO dto = prizeOpt.get();
                if (dto.getImage() != null) {
                    dto.setImage(null);
                }
                return ResponseEntity.ok(dto);
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.notFound().build();
        }
    }
    @GetMapping("/{id}/image")
    public ResponseEntity<?> getPrizeImage(@PathVariable int id) {
        try {
            Optional<PrizeDTO> prize = prizeManager.getPrizeById(id);
            if (prize.isPresent() && prize.get().getImage() != null) {
                return ResponseEntity.ok()
                        .contentType(MediaType.IMAGE_JPEG)
                        .body(prize.get().getImage());
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("")
    public ResponseEntity<PrizeDTO> createPrize(@RequestBody PrizeDTO prize) {
        try {
            prizeManager.savePrize(prize, null);
            return ResponseEntity.status(HttpStatus.CREATED).body(prize);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    // Update prize
    @PutMapping("/{id}")
    public ResponseEntity<PrizeDTO> updatePrize(@PathVariable int id, @RequestBody PrizeDTO prize, MultipartFile file) {
        try {
            prizeManager.updatePrizeDetails(prize, id, file);
            return ResponseEntity.ok(prize);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Delete prize
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePrize(@PathVariable int id) {
        Optional<PrizeDTO> prize = prizeManager.getPrizeById(id);
        if (prize.isPresent()) {
            prizeManager.deletePrize(prize.get().getId());
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    // Buy prize
    @PostMapping("/{id}/buy")
    public ResponseEntity<?> buyPrize(@PathVariable int id, @RequestBody User user) {
        Optional<PrizeDTO> prizeOpt = prizeManager.getPrizeById(id);
        if (!prizeOpt.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        try {
            PrizeDTO boughtPrize = userManager.buyPrize(prizeOpt.get() , userMapper.toDTO(user));
            return ResponseEntity.ok(boughtPrize);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}