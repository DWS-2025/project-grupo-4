package com.casino.grupo4_dws.casinoweb.managers;

import com.casino.grupo4_dws.casinoweb.dto.GameDTO;
import com.casino.grupo4_dws.casinoweb.mapper.GameMapper;
import com.casino.grupo4_dws.casinoweb.model.Game;
import com.casino.grupo4_dws.casinoweb.repos.GameRepository;
import jakarta.annotation.PostConstruct;
import org.hibernate.engine.jdbc.BlobProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.sql.rowset.serial.SerialBlob;

@Service
public class GameManager {

    @Autowired
    private GameRepository gameRepo;

    @Autowired
    private GameMapper gameMapper;

    private List<Game> gameList;

    public GameManager() {
        this.gameList = new ArrayList<Game>();
    }

    public void deleteGame(long id) {
        if (!gameRepo.existsById(id)) {
            throw new IllegalArgumentException("El juego introducido no existe");
        }
        gameRepo.deleteById(id);
        removeGameId((int)id);
    }

    public void removeGameId(int id) {
        gameList.removeIf(g -> g.getId() == id);
    }

    public Optional<GameDTO> getGameById(int id) {
        return gameRepo.findGameById(id)
                .map(game -> gameMapper.toDTO(game));
    }

    public GameDTO getGame(int id) {
        Game game = gameRepo.findGameById(id)
                .orElseThrow(() -> new IllegalArgumentException("El juego introducido no existe"));
        return gameMapper.toDTO(game);
    }

    @PostConstruct
    public void postConstruct() throws IOException, SQLException {
        GameDTO diceGame = new GameDTO();
        diceGame.setTitle("Dado");
        diceGame.setDescription("Tira un dado de seis caras y prueba tu suerte!");
        diceGame.setChance(16);
        diceGame.setMinInput(1);
        diceGame.setMultiplier(6);
        saveGame(diceGame, null);

        GameDTO rouletteGame = new GameDTO();
        rouletteGame.setTitle("Ruleta");
        rouletteGame.setDescription("Apuesta a tu color favorito y gira la ruleta!");
        rouletteGame.setChance(50);
        rouletteGame.setMinInput(1);
        rouletteGame.setMultiplier(2);
        saveGame(rouletteGame, null);

        GameDTO slotsGame = new GameDTO();
        slotsGame.setTitle("Tragaperras");
        slotsGame.setDescription("Las monedas en el bolsillo no te generan mas dinero... aquí si");
        slotsGame.setChance(5);
        slotsGame.setMinInput(10);
        slotsGame.setMultiplier(20);
        saveGame(slotsGame, null);
    }

    public List<GameDTO> getGameList() {
        return gameRepo.findAll().stream()
                .map(game -> gameMapper.toDTO(game))
                .collect(Collectors.toList());
    }

    public GameDTO saveGame(GameDTO gameDTO, MultipartFile imageFile) throws IOException {
        Game game = gameMapper.toEntity(gameDTO);

        if (imageFile != null && !imageFile.isEmpty()) {
            game.setImage(BlobProxy.generateProxy(imageFile.getInputStream(), imageFile.getSize()));
        }

        if (game.getChance() < 1 || game.getChance() > 100) {
            throw new IllegalArgumentException("Esta creacion de juego es ilegal");
        }

        Game savedGame = gameRepo.save(game);
        return gameMapper.toDTO(savedGame);
    }
}
