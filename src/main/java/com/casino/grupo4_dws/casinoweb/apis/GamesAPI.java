
package com.casino.grupo4_dws.casinoweb.apis;

import com.casino.grupo4_dws.casinoweb.dto.PrizeDTO;
import com.casino.grupo4_dws.casinoweb.managers.GameManager;
import com.casino.grupo4_dws.casinoweb.mapper.GameMapper;
import com.casino.grupo4_dws.casinoweb.model.Game;
import com.casino.grupo4_dws.casinoweb.dto.GameDTO;
import com.casino.grupo4_dws.casinoweb.mapper.GameMapper;
import com.casino.grupo4_dws.casinoweb.model.Prize;
import com.casino.grupo4_dws.casinoweb.managers.JWTManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/games")
public class GamesAPI {

    @Autowired
    private GameManager gameManager;
    @Autowired
    private GameMapper gameMapper;
    @Autowired
    private JWTManager jwtManager;


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
            @RequestHeader("Authorization") String jwtToken,
            @RequestPart("game") GameDTO gameDTO,
            @RequestPart(value = "image", required = false) MultipartFile imageFile) {
        if (jwtManager.tokenBelongsToAdmin(jwtManager.extractTokenFromHeader(jwtToken))) {
            try {
                Game game = gameMapper.toEntity(gameDTO);
                GameDTO savedGameDTO = gameManager.saveGame(game, imageFile);
                return ResponseEntity.status(HttpStatus.CREATED).body(savedGameDTO);
            } catch (Exception e) {
                return ResponseEntity.badRequest().build();
            }
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<GameDTO> updateGame(
            @RequestHeader("Authorization") String jwtToken,
            @PathVariable int id,
            @RequestPart("game") GameDTO gameDTO,
            @RequestPart(value = "imageFile", required = false) MultipartFile imageFile) {
        if (jwtManager.tokenBelongsToAdmin(jwtManager.extractTokenFromHeader(jwtToken))) {
            try {
                GameDTO updatedGame = gameManager.updateGameDetails(gameDTO, id, imageFile);
                return ResponseEntity.ok(updatedGame);
            } catch (Exception e) {
                e.printStackTrace();
                return ResponseEntity.notFound().build();
            }
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGame(
            @RequestHeader("Authorization") String jwtToken,
            @PathVariable int id) {
        if (jwtManager.tokenBelongsToAdmin(jwtManager.extractTokenFromHeader(jwtToken))) {
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
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
    @GetMapping("/game/{id}/image")
    public ResponseEntity<Resource> downloadImage(@PathVariable int id) throws SQLException {
        Optional<GameDTO> optionalGameDTO = gameManager.getGameById(id);

        if (optionalGameDTO.isPresent()) {
            Game game = gameMapper.toEntity(optionalGameDTO.get());
            Blob imageBlob = game.getImage();

            if (imageBlob != null) {
                InputStream inputStream = imageBlob.getBinaryStream();
                Resource resource = new InputStreamResource(inputStream);

                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"game_" + id + ".jpg\"")
                        .header(HttpHeaders.CONTENT_TYPE, "image/jpeg")
                        .contentLength(imageBlob.length())
                        .body(resource);
            }
        }

        return ResponseEntity.notFound().build();
    }

}
