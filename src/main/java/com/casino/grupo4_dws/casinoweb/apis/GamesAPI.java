package com.casino.grupo4_dws.casinoweb.apis;

import com.casino.grupo4_dws.casinoweb.managers.GameManager;
import com.casino.grupo4_dws.casinoweb.model.Game;
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

    @GetMapping("")
    public ResponseEntity<List<Game>> getAllGames() {
        List<Game> games = gameManager.getGameList();
        // Ensure images are properly handled before sending response
        games.forEach(game -> {
            if (game.getImage() != null) {
                game.setImage(null); // Temporarily remove image for JSON serialization
            }
        });
        return ResponseEntity.ok(games);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Game> getGame(@PathVariable int id) {
        Optional<Game> game = gameManager.getGameById(id);
        return game.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("")
    public ResponseEntity<Game> createGame(@RequestBody Game game) {
        try {
            gameManager.saveGame(game,null);
            return ResponseEntity.status(HttpStatus.CREATED).body(game);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Game> updateGame(@PathVariable int id, @RequestBody Game game) {
        try {
            if (gameManager.getGameById(id).isPresent()) {
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