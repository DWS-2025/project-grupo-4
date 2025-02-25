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
        Prize prize = new Prize("AWP Dragon Lore", 1500, "AWP Dragon Lore Souvenir FN", "/images/awp_lore.jpg", 100);
        addPrize(prize);
    }

    public void addPrize(Prize prize) {
        prizeList.add(prize);
    }

    public void removePrizeId(int id) {
        prizeList.removeIf(prize -> prize.getId() == id); // Asegúrate de usar getId()
    }

    public List<Prize> getPrizeList() {
        return prizeList;
    }

    public Prize getPrize(int id) {
        return prizeList.stream().filter(prize -> prize.getId() == id).findFirst().orElse(null);
    }
}
