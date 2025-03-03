package com.casino.grupo4_dws.casinoweb.services;

import com.casino.grupo4_dws.casinoweb.model.Game;
import com.casino.grupo4_dws.casinoweb.model.User;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserManager {

    public void addFavoriteGame(User user, Game game) {
        List<Game> gamesLiked = user.getGamesLiked();
        if (!gamesLiked.contains(game)) {
            gamesLiked.add(game);  // Añadir el juego a la lista de favoritos
        }
    }

    public void removeFavoriteGame(User user, Game game) {
        List<Game> gamesLiked = user.getGamesLiked();
        gamesLiked.remove(game);  // Eliminar el juego de la lista de favoritos
    }

    public List<Game> getFavoriteGames(User user) {
        return user.getGamesLiked();
    }
}
