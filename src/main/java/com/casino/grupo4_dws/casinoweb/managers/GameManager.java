package com.casino.grupo4_dws.casinoweb.managers;

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
import javax.sql.rowset.serial.SerialBlob;


@Service
public class GameManager {
    @Autowired
    private GameRepository gameRepo;

    private List<Game> gameList;

    public GameManager() {
        this.gameList = new ArrayList<Game>();
    }
    public void deleteGame(int id){
        Optional<Game> game = gameRepo.findGameById(id);
        if(game.isPresent()){
            gameRepo.delete(game.get());
        } else {
            throw new IllegalArgumentException("El juego introducido no existe");
        }
    }
    public void removeGameId(int id) {
        gameList.removeIf(g -> g.getId() == id);
    }

    public Optional<Game> getGameById(int id) {
        return gameRepo.findGameById(id);
    }
    public Game getGame(int id) {
        Optional<Game> game = gameRepo.findGameById(id);
        if (game.isPresent()) {
            return game.get();
        } else {
            throw new IllegalArgumentException("El juego introducido no existe");
        }
    }

    public void PostConstruct() throws IOException, SQLException {
        gameRepo.save(new Game("Dado", "Tira un dado de seis caras y prueba tu suerte!", new javax.sql.rowset.serial.SerialBlob(Files.readAllBytes(Paths.get("src/main/resources/static/images/dice.jpg"))), 16, 1, 6));
        gameRepo.save(new Game("Ruleta", "Apuesta a tu color favorito y gira la ruleta!\"", new javax.sql.rowset.serial.SerialBlob(Files.readAllBytes(Paths.get("src/main/resources/static/images/Ssegura.jpg"))), 50, 1, 2));
        gameRepo.save(new Game("Tragaperras", "Las monedas en el bolsillo no te generan mas dinero... aquí si", new javax.sql.rowset.serial.SerialBlob(Files.readAllBytes(Paths.get("src/main/resources/static/images/slots.jpg"))), 5, 10, 20));

        /*
        gameRepo.save(new Game("Dado", "Tira un dado de seis caras y prueba tu suerte!", "/images/dice.jpg", 16, 1, 6));
        gameRepo.save(new Game("Ruleta", , "/images/Ssegura.jpg", 50, 1, 2));
        gameRepo.save(new Game(, "/images/slots.jpg", 5, 10, 20));
        */
    }

    public List<Game> getGameList() {
        return gameRepo.findAll();
    }

    public void saveGame(Game game, MultipartFile imageFile) throws IOException {
        if(!imageFile.isEmpty()){
            game.setImage(BlobProxy.generateProxy(imageFile.getInputStream(), imageFile.getSize()));
        }
        gameRepo.save(game);
    }

}
