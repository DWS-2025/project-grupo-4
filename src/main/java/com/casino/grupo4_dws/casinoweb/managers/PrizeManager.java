package com.casino.grupo4_dws.casinoweb.managers;

import com.casino.grupo4_dws.casinoweb.dto.PrizeDTO;
import com.casino.grupo4_dws.casinoweb.mapper.PrizeMapper;
import com.casino.grupo4_dws.casinoweb.model.Prize;
import com.casino.grupo4_dws.casinoweb.repos.PrizeRepository;
import org.hibernate.engine.jdbc.BlobProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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

    public List<PrizeDTO> findAllPrizes() {
        return prizeRepo.findAllByOwnerIsNull().stream()
                .map(prize -> prizeMapper.toDTO(prize))
                .collect(Collectors.toList());
    }

    public List<PrizeDTO> findPrizesByFilters(String title, int minPrice, int maxPrice) {
        if (title != null && title.trim().isEmpty()) {
            title = null;
        }
        if(minPrice > maxPrice) {
            minPrice = maxPrice;
        }
        return prizeRepo.findByFilters(title, minPrice, maxPrice).stream()
                .map(prize -> prizeMapper.toDTO(prize))
                .collect(Collectors.toList());
    }

    public PrizeDTO savePrize(PrizeDTO prizeDTO, MultipartFile imageFile) throws IOException {
        Prize newPrize = prizeMapper.toEntity(prizeDTO);
        if (imageFile != null && !imageFile.isEmpty()) {
            newPrize.setImage(BlobProxy.generateProxy(imageFile.getInputStream(), imageFile.getSize()));
        }
        Prize savedPrize = prizeRepo.save(newPrize);
        return prizeMapper.toDTO(savedPrize);
    }

    public Optional<PrizeDTO> getPrizeById(int id) {
        return prizeRepo.findPrizeById(id)
                .map(prize -> prizeMapper.toDTO(prize));
    }

    public void deletePrize(long id) {
        prizeRepo.deleteById(id);
    }

    public PrizeDTO updatePrizeDetails(PrizeDTO updatedPrizeDTO, int id, MultipartFile imageFile) throws IOException {
        Prize prize = prizeRepo.findPrizeById(id)
                .orElseThrow(() -> new RuntimeException("Prize not found with id: " + id));

        prize.setTitle(updatedPrizeDTO.getTitle());
        prize.setDescription(updatedPrizeDTO.getDescription());
        prize.setPrice(updatedPrizeDTO.getPrice());

        if (imageFile != null && !imageFile.isEmpty()) {
            prize.setImage(BlobProxy.generateProxy(imageFile.getInputStream(), imageFile.getSize()));
        }

        Prize updatedPrize = prizeRepo.save(prize);
        return prizeMapper.toDTO(updatedPrize);
    }

    public void postConstruct(){
        try {
            PrizeDTO prize1 = new PrizeDTO();
            prize1.setTitle("AWP Dragon Lore");
            prize1.setPrice(1500);
            prize1.setDescription("AWP Dragon Lore Souvenir FN");
            savePrize(prize1, null);

            PrizeDTO prize2 = new PrizeDTO();
            prize2.setTitle("Viaje Deluxe");
            prize2.setPrice(3500);
            prize2.setDescription("Viaje deluxe a un pueblo perdido de la mano de dios por ahi para dos personas");
            savePrize(prize2, null);

            PrizeDTO prize3 = new PrizeDTO();
            prize3.setTitle("PlayStation 5");
            prize3.setPrice(2000);
            prize3.setDescription("Consola PlayStation 5 con dos mandos DualSense y 3 juegos a elegir");
            savePrize(prize3, null);

            PrizeDTO prize4 = new PrizeDTO();
            prize4.setTitle("Rolex Submariner");
            prize4.setPrice(5000);
            prize4.setDescription("Reloj Rolex Submariner Date en acero inoxidable, edición limitada");
            savePrize(prize4, null);

            PrizeDTO prize5 = new PrizeDTO();
            prize5.setTitle("MacBook Pro");
            prize5.setPrice(3000);
            prize5.setDescription("MacBook Pro 16' con chip M2 Pro, 32GB RAM y 1TB SSD");
            savePrize(prize5, null);

            PrizeDTO prize6 = new PrizeDTO();
            prize6.setTitle("RTX 4090");
            prize6.setPrice(2500);
            prize6.setDescription("NVIDIA GeForce RTX 4090 24GB GDDR6X Gaming OC");
            savePrize(prize6, null);

            PrizeDTO prize7 = new PrizeDTO();
            prize7.setTitle("Viaje a Las Vegas");
            prize7.setPrice(4000);
            prize7.setDescription("Viaje todo incluido a Las Vegas para dos personas, 7 días en hotel 5 estrellas");
            savePrize(prize7, null);

            PrizeDTO prize8 = new PrizeDTO();
            prize8.setTitle("iPhone 15 Pro Max");
            prize8.setPrice(2000);
            prize8.setDescription("iPhone 15 Pro Max 256GB Titanio Natural con AppleCare+");
            savePrize(prize8, null);

            PrizeDTO prize9 = new PrizeDTO();
            prize9.setTitle("Cena Gourmet");
            prize9.setPrice(1000);
            prize9.setDescription("Experiencia gastronómica para dos en restaurante con 3 estrellas Michelin");
            savePrize(prize9, null);
        } catch (IOException e) {
            System.err.println("Error al leer el archivo de imagen: " + e.getMessage());
        }
    }
    public Page<Prize> findAllPrizes(Pageable pageable) {
        return prizeRepo.findAllByOwnerIsNull(pageable);
    }
}
