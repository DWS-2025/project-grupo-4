package com.casino.grupo4_dws.casinoweb.managers;

import com.casino.grupo4_dws.casinoweb.model.Game;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
public class GameManager {

    private List<Game> gameList;

    public GameManager() {
        this.gameList = new ArrayList<Game>();
    }



    public void addGame(Game game) {
        gameList.add(game);
    }

    public void removeGameId(int id) {
        gameList.removeIf(g -> g.getId() == id);
    }

    public Game getGame(int id) {
        return gameList.stream().filter(g -> g.getId() == id).findFirst().orElse(null);
    }

    public List<Game> getGameList() {
        return gameList;
    }

}
