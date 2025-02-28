package com.casino.grupo4_dws.casinoweb.services;

import com.casino.grupo4_dws.casinoweb.model.Prize;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PrizeManager {

    private List<Prize> prizeList;

    public PrizeManager() {
        this.prizeList = new ArrayList<>();
    }

    @PostConstruct
    private void startPrizes() {
        Prize DLore = new Prize("AWP Dragon Lore", 1500, "AWP Dragon Lore Souvenir FN", "/images/albacete.jpg", 100);
        addPrize(DLore);
    }

    public void addPrize(Prize prize) {
        int newId = prizeList.size();
        prize.setId(newId);
        prizeList.add(prize);
    }

    public void removePrizeId(int id) {
        prizeList.removeIf(prize -> prize.getId() == id);
        for (int i = 0; i < prizeList.size(); i++) {
            prizeList.get(i).setId(i + 1); // Reasignar IDs basados en la posición
        }
    }

    public List<Prize> getPrizeList() {
        return prizeList;
    }

    public void updatePrizeWOImage(Prize updatedPrize, int id) {
        Prize prize = prizeList.get(id);
        prize.setTitle(updatedPrize.getTitle());
        prize.setDescription(updatedPrize.getDescription());
        prize.setPrice(updatedPrize.getPrice());
    }
    public void updatePrizeNImage(Prize updatedPrize, int id) {
        Prize prize = prizeList.get(id);
        prize.setTitle(updatedPrize.getTitle());
        prize.setDescription(updatedPrize.getDescription());
        prize.setImage(updatedPrize.getImage());
        prize.setPrice(updatedPrize.getPrice());
    }
    public Prize getPrize(int id) {
        return prizeList.stream().filter(prize -> prize.getId() == id).findFirst().orElse(null);
    }
}
