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
        List<Bet> bets = betManager.findAll();
        return ResponseEntity.ok(betMapper.toDTOList(bets));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BetDTO> getBet(@PathVariable int id) {
        Optional<Bet> bet = betManager.findById(id);
        return bet.map(b -> ResponseEntity.ok(betMapper.toDTO(b)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("")
    public ResponseEntity<BetDTO> createBet(@RequestBody BetDTO betDTO) {
        try {
            Bet bet = betMapper.toEntity(betDTO);
            betManager.Save(bet);
            return ResponseEntity.status(HttpStatus.CREATED).body(betMapper.toDTO(bet));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<BetDTO> updateBet(@PathVariable int id, @RequestBody BetDTO betDTO) {
        try {
            if (betManager.findById(id).isPresent()) {
                Bet bet = betMapper.toEntity(betDTO);
                bet.setId(id);
                betManager.Save(bet);
                return ResponseEntity.ok(betMapper.toDTO(bet));
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