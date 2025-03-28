package com.casino.grupo4_dws.casinoweb.managers;

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

@Service
public class PrizeManager {

    @Autowired
    private PrizeRepository prizeRepo;

    public List<Prize> findAllPrizes() {
        return prizeRepo.findAllByOwnerIsNull();
    }

    public Prize save(Prize prize) {
        return prizeRepo.save(prize);
    }

    public Optional<Prize> getPrizeById(int id) {
        return prizeRepo.findPrizeById(id);
    }

    public void deletePrize(Prize prize) {
        prizeRepo.delete(prize);
    }

    public void updatePrizeDetails(Prize updatedPrize, int id) {
        Prize prize = prizeRepo.findPrizeById(id)
                .orElseThrow(() -> new RuntimeException("Prize not found with id: " + id));

        prize.setTitle(updatedPrize.getTitle());
        prize.setDescription(updatedPrize.getDescription());
        prize.setPrice(updatedPrize.getPrice());

        if (updatedPrize.getImage() != null) {
            prize.setImage(updatedPrize.getImage());
        }
        prizeRepo.save(prize);
    }

    public void postConstruct() throws IOException, SQLException {
        prizeRepo.save(new Prize("AWP Dragon Lore", 1500, "AWP Dragon Lore Souvenir FN", new javax.sql.rowset.serial.SerialBlob(Files.readAllBytes(Paths.get( "src/main/resources/static/images/awp_lore.png")))));
        prizeRepo.save(new Prize("Viaje Deluxe",3500,"Viaje deluxe a un pueblo perdido de la mano de dios por ahi para dos personas", new javax.sql.rowset.serial.SerialBlob(Files.readAllBytes(Paths.get("src/main/resources/static/images/albacete.jpg")))));
    }

    public void savePrize(Prize prize, MultipartFile imageFile) throws IOException {
        if(!imageFile.isEmpty()){
            prize.setImage(BlobProxy.generateProxy(imageFile.getInputStream(), imageFile.getSize()));
        }
        prizeRepo.save(prize);
    }
}
