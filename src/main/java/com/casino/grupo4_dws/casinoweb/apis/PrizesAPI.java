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
import com.casino.grupo4_dws.casinoweb.managers.JWTManager;
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

import org.springframework.http.MediaType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import javax.sql.rowset.serial.SerialBlob;
import java.sql.Blob;

@RestController
@RequestMapping("/api/prizes")
public class PrizesAPI {

    @Autowired
    private PrizeManager prizeManager;

    @Autowired
    private PrizeMapper prizeMapper;

    @Autowired
    private JWTManager jwtManager;

    @GetMapping("")
    public ResponseEntity<?> getAllPrizes(Pageable pageable, String title, int minPrice, int maxPrice) {

        try {
            Page<Prize> prizePage = prizeManager.findAllPrizes(pageable);
            if (prizePage.isEmpty()) {
                return ResponseEntity.noContent().build();
            }

            Page<PrizeDTO> dtoPage = prizePage.map(prize -> {
                PrizeDTO dto = prizeMapper.toDTO(prize);
                // Prevent serialization issues with Blob/InputStream
                if (prizeMapper.toEntity(dto).getImage() != null) {
                    prizeMapper.toEntity(dto).setImage(null);
                }
                return dto;
            });

            return ResponseEntity.ok(dtoPage);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Error retrieving prizes: " + e.getMessage());
        }
    }

    @GetMapping("/search")
    public ResponseEntity<?> getAllPrizesFiltered(Pageable pageable, @RequestParam(required = false) String title,
                                                  @RequestParam(required = false, defaultValue = "0") Integer minPrice,
                                                  @RequestParam(required = false, defaultValue = "999999") Integer maxPrice) {

        try {
            Page<Prize> prizePage = prizeManager.findPrizesPageByFilter(pageable, title, minPrice, maxPrice);
            if (prizePage.isEmpty()) {
                return ResponseEntity.noContent().build();
            }

            Page<PrizeDTO> dtoPage = prizePage.map(prize -> {
                PrizeDTO dto = prizeMapper.toDTO(prize);
                // Prevent serialization issues with Blob/InputStream
                if (prizeMapper.toEntity(dto).getImage() != null) {
                    prizeMapper.toEntity(dto).setImage(null);
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
                if (prizeMapper.toEntity(dto).getImage() != null) {
                    prizeMapper.toEntity(dto).setImage(null);
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
            if (prize.isPresent() && prizeMapper.toEntity(prize.get()) != null) {
                return ResponseEntity.ok()
                        .contentType(MediaType.IMAGE_JPEG)
                        .body(prizeMapper.toEntity(prize.get()).getImage());
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PrizeDTO> createPrize(
            @RequestHeader("Authorization") String jwtToken,
            @RequestPart("prize") PrizeDTO prizeDTO,
            @RequestPart(value = "image", required = false) MultipartFile imageFile) {
        if (jwtManager.tokenBelongsToAdmin(jwtManager.extractTokenFromHeader(jwtToken))) {
            try {
                Prize prize = prizeMapper.toEntity(prizeDTO);
                PrizeDTO savedPrizeDTO = prizeManager.savePrize(prize, imageFile);
                return ResponseEntity.status(HttpStatus.CREATED).body(savedPrizeDTO);
            } catch (IOException e) {
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
            } catch (Exception e) {
                e.printStackTrace();
                return ResponseEntity.badRequest().build();
            }
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    // Update prize
    @PutMapping("/{id}")
    public ResponseEntity<PrizeDTO> updatePrize(
            @RequestHeader("Authorization") String jwtToken,
            @PathVariable int id,
            @RequestBody PrizeDTO prize,
            MultipartFile file) {
        if (jwtManager.tokenBelongsToAdmin(jwtManager.extractTokenFromHeader(jwtToken))) {
            try {
                prizeManager.updatePrizeDetails(prize, id, file);
                return ResponseEntity.ok(prize);
            } catch (Exception e) {
                return ResponseEntity.notFound().build();
            }
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    // Delete prize
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePrize(
            @RequestHeader("Authorization") String jwtToken,
            @PathVariable int id) {
        if (jwtManager.tokenBelongsToAdmin(jwtManager.extractTokenFromHeader(jwtToken))) {
            Optional<PrizeDTO> prize = prizeManager.getPrizeById(id);
            if (prize.isPresent()) {
                prizeManager.deletePrize(prizeMapper.toEntity(prize.get()).getId());
                return ResponseEntity.ok().build();
            }
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
/*
    // Buy prize
    @PostMapping("/{id}/buy")
    public ResponseEntity<?> buyPrize(@PathVariable int id, @RequestBody User user) {
        Optional<PrizeDTO> prizeOpt = prizeManager.getPrizeById(id);
        if (!prizeOpt.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        try {
            UserDTO boughtPrize = userManager.buyPrize(prizeOpt.get() , userMapper.toDTO(user));
            return ResponseEntity.ok(boughtPrize);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }*/
}