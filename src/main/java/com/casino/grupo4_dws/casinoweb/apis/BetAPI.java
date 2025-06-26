package com.casino.grupo4_dws.casinoweb.apis;

import com.casino.grupo4_dws.casinoweb.dto.GameDTO;
import com.casino.grupo4_dws.casinoweb.dto.UserDTO;
import com.casino.grupo4_dws.casinoweb.managers.BetManager;
import com.casino.grupo4_dws.casinoweb.managers.GameManager;
import com.casino.grupo4_dws.casinoweb.managers.UserManager;
import com.casino.grupo4_dws.casinoweb.model.Bet;
import com.casino.grupo4_dws.casinoweb.dto.BetDTO;
import com.casino.grupo4_dws.casinoweb.mapper.BetMapper;
import com.casino.grupo4_dws.casinoweb.managers.JWTManager;
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
    @Autowired
    private GameManager gameManager;
    @Autowired
    private UserManager userManager;
    @Autowired
    private JWTManager jwtManager;

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
    public ResponseEntity<BetDTO> createBet(@RequestHeader("Authorization") String jwtToken, @RequestBody BetDTO betDTO) {
        if (jwtManager.tokenBelongsToAdmin(jwtManager.extractTokenFromHeader(jwtToken))) {
            try {
                betManager.save(betMapper.toEntity(betDTO));
                return ResponseEntity.status(HttpStatus.CREATED).body(betDTO);
            } catch (Exception e) {
                return ResponseEntity.badRequest().build();
            }
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<BetDTO> updateBet(@RequestHeader("Authorization") String jwtToken, @PathVariable int id, @RequestBody BetDTO betDTO) {
        if (jwtManager.tokenBelongsToAdmin(jwtManager.extractTokenFromHeader(jwtToken))) {
            try {
                betManager.updateBet(betDTO, id);
                return ResponseEntity.ok(betDTO);
            } catch (Exception e) {
                return ResponseEntity.badRequest().build();
            }
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBet(@RequestHeader("Authorization") String jwtToken, @PathVariable int id) {
        if (jwtManager.tokenBelongsToAdmin(jwtManager.extractTokenFromHeader(jwtToken))) {
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
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    @PutMapping("/play/{gameId}/user/{userId}")
    public ResponseEntity<BetDTO> PlayBet(@RequestHeader("Authorization") String jwtToken, @PathVariable int gameId, @PathVariable int userId, @RequestBody int amount) {
        if (jwtManager.tokenHasPermission(jwtManager.extractTokenFromHeader(jwtToken), userId)) {
            Optional<GameDTO> gameDTO = gameManager.getGameById(gameId);
            Optional<UserDTO> userDTO = userManager.findById(userId);

            if (gameDTO.isPresent() && userDTO.isPresent()) {
                BetDTO betDTO = betManager.playBet(gameDTO.get(), userDTO.get(), amount);
                return ResponseEntity.ok(betDTO);
            } else {
                return ResponseEntity.notFound().build();
            }
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
}