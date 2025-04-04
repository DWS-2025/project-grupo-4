package com.casino.grupo4_dws.casinoweb.apis;

import com.casino.grupo4_dws.casinoweb.managers.BetManager;
import com.casino.grupo4_dws.casinoweb.model.Bet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/bets")
public class BetAPI {

    @Autowired
    private BetManager betManager;

    @GetMapping("")
    public ResponseEntity<List<Bet>> getAllBets() {
        return ResponseEntity.ok(betManager.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Bet> getBet(@PathVariable int id) {
        Optional<Bet> bet = betManager.findById(id);
        return bet.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("")
    public ResponseEntity<Bet> createBet(@RequestBody Bet bet) {
        try {
            betManager.Save(bet);
            return ResponseEntity.status(HttpStatus.CREATED).body(bet);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Bet> updateBet(@PathVariable int id, @RequestBody Bet bet) {
        try {
            if (betManager.findById(id).isPresent()) {
                bet.setId(id);
                betManager.Save(bet);
                return ResponseEntity.ok(bet);
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBet(@PathVariable int id) {
        try {
            if (betManager.findById(id).isPresent()) {
                betManager.delete(id);
                return ResponseEntity.ok().build();
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}