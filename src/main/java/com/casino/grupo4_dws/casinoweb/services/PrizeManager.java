package com.casino.grupo4_dws.casinoweb.services;

import com.casino.grupo4_dws.casinoweb.model.Prize;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
public class PrizeManager {

    private List <Prize> prizeList;

    public PrizeManager() {
        this.prizeList = new ArrayList<Prize>();
    }

    @PostConstruct
    private void startPrizes(){
        Prize prize = new Prize("AWP Dragon Lore", 1500, "AWP Dragon Lore Souvenir FN", "/static/images/awp_lore.jpg",100);
        addPrize(prize);
    }

    public void addPrize(Prize prize){
        prizeList.add(prize);
    }

    public void removePrizeId(int id){
        prizeList.removeIf(prize -> prize.GetId() == id);
    }

    public List<Prize> getPrizeList(){
        return prizeList;
    }

}
