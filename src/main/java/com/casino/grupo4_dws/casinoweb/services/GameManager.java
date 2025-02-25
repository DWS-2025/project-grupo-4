package com.casino.grupo4_dws.casinoweb.services;

import com.casino.grupo4_dws.casinoweb.model.Game;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
public class GameManager {

    private List <Game> gameList;

    public GameManager() {
        this.gameList = new ArrayList<Game>();
    }

    @PostConstruct
    private void startGames(){
        Game TirarDado = new Game(1, "Dado", "Tira un dado de seis caras y prueba tu suerte!", "/images/", 16, 0, 6);
        Game Ruleta = new Game(2, "Ruleta", "Apuesta a tu número favorito y gira la ruleta!", "/images/Ssegura.jpg", 20, 1, 36);
        Game Blackjack = new Game(3, "Blackjack", "Juega contra el crupier y llega lo más cerca posible a 21!", "/images/blackjack.jpg", 25, 5, 2);
        
        addGame(TirarDado);
        addGame(Ruleta);
        addGame(Blackjack);
    }

    public void addGame(Game game){
        gameList.add(game);
    }

    public void removeGameId(int id){
        gameList.removeIf(g -> g.getId() == id);
    }

    public List<Game> getGameList(){
        return gameList;
    }

}
