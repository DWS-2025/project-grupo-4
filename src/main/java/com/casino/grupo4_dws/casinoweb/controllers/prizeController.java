package com.casino.grupo4_dws.casinoweb.controllers;

import com.casino.grupo4_dws.casinoweb.model.Prize;
import com.casino.grupo4_dws.casinoweb.services.PrizeManager;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/Prizes")
public class prizeController {

    private final PrizeManager prizeManager;

    public prizeController(PrizeManager prizeManager) {
        this.prizeManager = prizeManager;
    }

    @GetMapping
    public List<Prize> obtainPrizes() {
        return prizeManager.getPrizeList();
    }

    @GetMapping("/{id}")
    public Prize getPrize(@PathVariable int id) {
        return prizeManager.getPrize(id);
    }
}
