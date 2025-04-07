package com.casino.grupo4_dws.casinoweb.apis;

import com.casino.grupo4_dws.casinoweb.managers.GameManager;
import com.casino.grupo4_dws.casinoweb.mapper.GameMapper;
import com.casino.grupo4_dws.casinoweb.model.Game;
import com.casino.grupo4_dws.casinoweb.dto.GameDTO;
import com.casino.grupo4_dws.casinoweb.mapper.GameMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/games")
public class GamesAPI {

    @Autowired
    private GameManager gameManager;
    @Autowired
    private GameMapper gameMapper;


    @GetMapping("")
    public ResponseEntity<List<GameDTO>> getAllGames() {
        List<GameDTO> games = gameManager.getGameList();
        return ResponseEntity.ok(games);
    }

    @GetMapping("/{id}")
    public ResponseEntity<GameDTO> getGame(@PathVariable int id) {
        Optional<GameDTO> game = gameManager.getGameById(id);
        return game.map(g -> ResponseEntity.ok(g))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("")
    public ResponseEntity<GameDTO> createGame(@RequestBody GameDTO gameDTO) {
        try {
            GameDTO game = gameDTO;
            gameManager.saveGame(game,null);
            return ResponseEntity.status(HttpStatus.CREATED).body(game);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<GameDTO> updateGame(@PathVariable int id, @RequestBody GameDTO gameDTO) {
        try {
            if (gameManager.getGameById(id).isPresent()) {
                GameDTO game = gameDTO;
                game.setId(id);
                gameManager.saveGame(game,null);
                return ResponseEntity.ok(game);
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGame(@PathVariable int id) {
        try {
            if (gameManager.getGameById(id).isPresent()) {
                gameManager.deleteGame(id);
                return ResponseEntity.ok().build();
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}