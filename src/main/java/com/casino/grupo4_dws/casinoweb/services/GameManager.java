package com.casino.grupo4_dws.casinoweb.services;

import com.casino.grupo4_dws.casinoweb.model.Game;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
public class

GameManager {

    private List <Game> gameList;

    public GameManager() {
        this.gameList = new ArrayList<Game>();
    }

    @PostConstruct
    private void startGames(){
        Game TirarDado = new Game(1, "Dado", "Tira un dado de seis caras!", "/images/dados.png", 16,0,6);
        addGame(TirarDado);
    }

    public void addGame(Game game){
        int newId = gameList.size() + 1;

        game.setId(newId);
        gameList.add(game);
    }

    public void removeGameId(int id){
        gameList.removeIf(g -> g.getId() == id);
        for (int i = 0; i < gameList.size(); i++) {
            gameList.get(i).setId(i + 1); // Reasignar IDs basados en la posición
        }
    }
    public Game getGame(int id){
        return gameList.stream().filter(g -> g.getId() == id).findFirst().orElse(null);
    }

    public List<Game> getGameList(){
        return gameList;
    }

}
