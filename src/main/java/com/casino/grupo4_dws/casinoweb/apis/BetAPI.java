package com.casino.grupo4_dws.casinoweb.apis;

import com.casino.grupo4_dws.casinoweb.managers.BetManager;
import com.casino.grupo4_dws.casinoweb.model.Bet;
import com.casino.grupo4_dws.casinoweb.dto.BetDTO;
import com.casino.grupo4_dws.casinoweb.mapper.BetMapper;
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

    @Autowired
    private BetMapper betMapper;

    @GetMapping("")
    public ResponseEntity<List<BetDTO>> getAllBets() {
        List<BetDTO> bets = betManager.findAll();
        return ResponseEntity.ok(bets);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BetDTO> getBet(@PathVariable int id) {
        Optional<BetDTO> bet = betManager.findById(id);
        return bet.map(b -> ResponseEntity.ok(b))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("")
    public ResponseEntity<BetDTO> createBet(@RequestBody BetDTO betDTO) {
        try {
            betManager.save(betMapper.toEntity(betDTO));
            return ResponseEntity.status(HttpStatus.CREATED).body(betDTO);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<BetDTO> updateBet(@PathVariable int id, @RequestBody BetDTO betDTO) {
        try {
            betManager.updateBet(betDTO, id);
            return ResponseEntity.ok(betDTO);
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