
package com.casino.grupo4_dws.casinoweb.apis;

import com.casino.grupo4_dws.casinoweb.dto.PrizeDTO;
import com.casino.grupo4_dws.casinoweb.managers.GameManager;
import com.casino.grupo4_dws.casinoweb.mapper.GameMapper;
import com.casino.grupo4_dws.casinoweb.model.Game;
import com.casino.grupo4_dws.casinoweb.dto.GameDTO;
import com.casino.grupo4_dws.casinoweb.mapper.GameMapper;
import com.casino.grupo4_dws.casinoweb.model.Prize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<GameDTO> createPrize(
            @RequestPart("game") GameDTO gameDTO,
            @RequestPart(value = "image", required = false) MultipartFile imageFile){
        try {
            Game game = gameMapper.toEntity(gameDTO);
            GameDTO savedGameDTO = gameManager.saveGame(game,imageFile);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedGameDTO);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<GameDTO> updateGame(
            @PathVariable int id,
            @RequestPart("game") GameDTO gameDTO,
            @RequestPart(value = "imageFile", required = false) MultipartFile imageFile) {

        try {
            GameDTO updatedGame = gameManager.updateGameDetails(gameDTO, id, imageFile);
            return ResponseEntity.ok(updatedGame);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.notFound().build();
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
