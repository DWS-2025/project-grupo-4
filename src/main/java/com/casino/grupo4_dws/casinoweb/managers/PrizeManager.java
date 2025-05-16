package com.casino.grupo4_dws.casinoweb.managers;

import com.casino.grupo4_dws.casinoweb.dto.PrizeDTO;
import com.casino.grupo4_dws.casinoweb.mapper.PrizeMapper;
import com.casino.grupo4_dws.casinoweb.model.Prize;
import com.casino.grupo4_dws.casinoweb.repos.PrizeRepository;
import com.casino.grupo4_dws.casinoweb.repos.UserRepository;
import org.hibernate.engine.jdbc.BlobProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PrizeManager {

    @Autowired
    private PrizeRepository prizeRepo;
    @Autowired
    private PrizeMapper prizeMapper;
    @Autowired
    private UserRepository userRepo;

    public List<PrizeDTO> findAllPrizes() {
        return prizeMapper.toDTOList(prizeRepo.findAll());
    }

    public Optional<PrizeDTO> findById(@PathVariable int id) {
        return prizeRepo.findPrizeById(id)
                .map(user -> prizeMapper.toDTO(user));
    }

    public List<PrizeDTO> findPrizesByFilters(String title, int minPrice, int maxPrice) {
        if (title != null && title.trim().isEmpty()) {
            title = null;
        }
        if (minPrice > maxPrice) {
            minPrice = maxPrice;
        }
        return prizeRepo.findByFilters(title, minPrice, maxPrice).stream()
                .map(prize -> prizeMapper.toDTO(prize))
                .collect(Collectors.toList());
    }

    public PrizeDTO savePrize(Prize prize, MultipartFile imageFile) throws IOException {
        if (imageFile != null && !imageFile.isEmpty()) {
            prize.setImage(BlobProxy.generateProxy(imageFile.getInputStream(), imageFile.getSize()));
        }
        Prize savedPrize = prizeRepo.save(prize);
        return prizeMapper.toDTO(savedPrize);
    }

    public Optional<PrizeDTO> getPrizeById(int id) {
        return prizeRepo.findPrizeById(id)
                .map(prize -> prizeMapper.toDTO(prize));
    }

    public void deletePrize(int id) {
        Prize prize = prizeRepo.getPrizeById(id).get();
        if(prize.getOwner() == null){
            prizeRepo.deleteById((long)id);
        } else {
            prize.getOwner().getInventory().remove(prize);
            prizeRepo.deleteById((long)id);
            userRepo.save(prize.getOwner());
        }
    }

    public PrizeDTO updatePrizeDetails(PrizeDTO updatedPrizeDTO, int id, MultipartFile imageFile) throws IOException {
        Prize prize = prizeRepo.findPrizeById(id)
                .orElseThrow(() -> new RuntimeException("Prize not found with id: " + id));
        Prize updatedPrize = prizeMapper.toEntity(updatedPrizeDTO);

        prize.setTitle(updatedPrize.getTitle());
        prize.setDescription(updatedPrize.getDescription());
        prize.setPrice(updatedPrize.getPrice());

        if (imageFile != null && !imageFile.isEmpty()) {
            prize.setImage(BlobProxy.generateProxy(imageFile.getInputStream(), imageFile.getSize()));
        }

        prizeRepo.save(prize);
        return prizeMapper.toDTO(updatedPrize);
    }

    public void postConstruct() {
        try {
            Prize prize1 = new Prize();
            prize1.setTitle("AWP Dragon Lore");
            prize1.setPrice(1500);
            prize1.setDescription("AWP Dragon Lore Souvenir FN");
            savePrize(prize1, new MockMultipartFile("file", "awplore.jpg", "image/jpeg", Files.readAllBytes(Paths.get("src/main/resources/static/images/awp_lore.png"))));

            Prize prize2 = new Prize();
            prize2.setTitle("Viaje Deluxe");
            prize2.setPrice(3500);
            prize2.setDescription("Viaje deluxe a un pueblo perdido de la mano de dios por ahi para dos personas");
            savePrize(prize2, new MockMultipartFile("file", "albacete.jpg", "image/jpeg", Files.readAllBytes(Paths.get("src/main/resources/static/images/albacete.jpg"))));

            Prize prize3 = new Prize();
            prize3.setTitle("PlayStation 5");
            prize3.setPrice(2000);
            prize3.setDescription("Consola PlayStation 5 con dos mandos DualSense y 3 juegos a elegir");
            savePrize(prize3, new MockMultipartFile("file", "ps5.jpg", "image/jpeg", Files.readAllBytes(Paths.get("src/main/resources/static/images/ps5.jpg"))));

            Prize prize4 = new Prize();
            prize4.setTitle("Rolex Submariner");
            prize4.setPrice(5000);
            prize4.setDescription("Reloj Rolex Submariner Date en acero inoxidable, edición limitada");
            savePrize(prize4, new MockMultipartFile("file", "Rolex.jpg", "image/jpeg", Files.readAllBytes(Paths.get("src/main/resources/static/images/rolex.jpg"))));

            Prize prize5 = new Prize();
            prize5.setTitle("MacBook Pro");
            prize5.setPrice(3000);
            prize5.setDescription("MacBook Pro 16' con chip M2 Pro, 32GB RAM y 1TB SSD");
            savePrize(prize5, new MockMultipartFile("file", "macbook.jpg", "image/jpeg", Files.readAllBytes(Paths.get("src/main/resources/static/images/macbook.jpg"))));

            Prize prize6 = new Prize();
            prize6.setTitle("RTX 4090");
            prize6.setPrice(2500);
            prize6.setDescription("NVIDIA GeForce RTX 4090 24GB GDDR6X Gaming OC");
            savePrize(prize6, new MockMultipartFile("file", "rtx4090.jpg", "image/jpeg", Files.readAllBytes(Paths.get("src/main/resources/static/images/rtx4090.jpg"))));

            Prize prize7 = new Prize();
            prize7.setTitle("Viaje a Las Vegas");
            prize7.setPrice(4000);
            prize7.setDescription("Viaje todo incluido a Las Vegas para dos personas, 7 días en hotel 5 estrellas");
            savePrize(prize7, new MockMultipartFile("file", "vegas.jpg", "image/jpeg", Files.readAllBytes(Paths.get("src/main/resources/static/images/vegas.jpg"))));

            Prize prize8 = new Prize();
            prize8.setTitle("iPhone 15 Pro Max");
            prize8.setPrice(2000);
            prize8.setDescription("iPhone 15 Pro Max 256GB Titanio Natural con AppleCare+");
            savePrize(prize8, new MockMultipartFile("file", "iphone.jpg", "image/jpeg", Files.readAllBytes(Paths.get("src/main/resources/static/images/iphone.jpg"))));

            Prize prize9 = new Prize();
            prize9.setTitle("Cena Gourmet");
            prize9.setPrice(1000);
            prize9.setDescription("Experiencia gastronómica para dos en restaurante con 3 estrellas Michelin");
            savePrize(prize9, new MockMultipartFile("file", "provolone.jpg", "image/jpeg", Files.readAllBytes(Paths.get("src/main/resources/static/images/provolone.jpg"))));
        } catch (IOException e) {
            System.err.println("Error al leer el archivo de imagen: " + e.getMessage());
        }
    }

    public Page<Prize> findAllPrizes(Pageable pageable) {
        return prizeRepo.findAllByOwnerIsNull(pageable);
    }

    public Page<Prize> findPrizesPageByFilter(Pageable pageable, String title, int minPrice, int maxPrice) {
        if (title != null && title.trim().isEmpty()) {
            title = null;
        }
        if (minPrice > maxPrice) {
            minPrice = maxPrice;
        }
        return prizeRepo.findPageByFilters(title,minPrice,maxPrice,pageable);
    }
}
