package com.casino.grupo4_dws.casinoweb.managers;

import com.casino.grupo4_dws.casinoweb.dto.PrizeDTO;
import com.casino.grupo4_dws.casinoweb.mapper.PrizeMapper;
import com.casino.grupo4_dws.casinoweb.model.Prize;
import com.casino.grupo4_dws.casinoweb.repos.PrizeRepository;
import org.hibernate.engine.jdbc.BlobProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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
        } catch (IOException e) {
            System.err.println("Error al leer el archivo de imagen: " + e.getMessage());
        }
    }
}
