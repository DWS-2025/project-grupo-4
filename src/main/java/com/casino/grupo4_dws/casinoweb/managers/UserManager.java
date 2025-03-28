package com.casino.grupo4_dws.casinoweb.managers;

import com.casino.grupo4_dws.casinoweb.model.Game;
import com.casino.grupo4_dws.casinoweb.model.Prize;
import com.casino.grupo4_dws.casinoweb.model.User;
import com.casino.grupo4_dws.casinoweb.repos.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserManager {

    @Autowired
    private UserRepository userRepo;
    public List<User> findAll() {
        return userRepo.findAll();
    }
    public User save(User user) {
        return userRepo.save(user);
    }
    public void postConstruct(){
        userRepo.save(new User("gigandres","1234",5000,true));
        userRepo.save(new User("ralpi","qwerty",10000,true));
        userRepo.save(new User("user","aaaaa",500,false));
        userRepo.save(new User("userprize","1234",1500,false));
        userRepo.save(new User("saultj", "abc", 9999999, true));
    }
    public Optional<User> findByUsername(String username) {
        return userRepo.getUserByUserName(username);
    }
    public boolean isUserCorrect(String username, String password) {
        Optional<User> user = userRepo.getUserByUserName(username);
        if (user.isPresent()) {
            if (user.get().getPassword().equals(password)) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }
    public void saveUser(String username, String password) {
        Optional<User> user = userRepo.getUserByUserName(username);
        if (user.isPresent()) {
            throw new IllegalArgumentException("Ya existe un usuario con ese nombre");
        }
        userRepo.save(new User(username,password,500,false));
    }

    @Transactional
    public Prize buyPrize(Prize prize, User user) {
        if (user.getInventory() == null) {
            user.setInventory(new ArrayList<>());
        }
        if (user.getInventory().contains(prize)) {
            throw new IllegalArgumentException("Ya tienes el producto comprado");
        }
        if (prize.getPrice() > user.getMoney()) {
            throw new IllegalArgumentException("No tienes suficiente dinero");
        }
        if (prize.getOwner() != null && !prize.getOwner().equals(user)) {
            throw new IllegalArgumentException("Este premio ya pertenece a otro usuario");
        }
        user.getInventory().add(prize);
        prize.setOwner(user);
        user.setMoney(user.getMoney() - prize.getPrice());
        try {
            userRepo.save(user);
        } catch (Exception e) {
            throw new RuntimeException("Error al guardar el usuario despues de comprar");
        }

        return prize;
    }
    public User getUserWithGamesLiked(int id) {
        return userRepo.findByIdWithGamesLiked(id);
    }
    public User findByIdMeta(int id) {
        return userRepo.findByIdWithGamesLiked(id);
    }

    public void setFav(User user, Game game) {
        if (game.getUsersLiked() == null) {
            game.setUsersLiked(new ArrayList<>());
        }
        if (user.getGamesLiked() == null) {
            user.setGamesLiked(new ArrayList<>());
        }
        if (user.getGamesLiked().contains(game)) {
            throw new IllegalArgumentException("Ya tienes el juego en favoritos");
        }
        user.getGamesLiked().add(game);
        userRepo.save(user);
    }

    @Transactional
    public void deleteFav(User user, Game game) {
        if (game.getUsersLiked() == null) {
            game.setUsersLiked(new ArrayList<>());
        }
        if (user.getGamesLiked() == null) {
            user.setGamesLiked(new ArrayList<>());
        }

        // Force load the collections
        user.getGamesLiked().size();
        game.getUsersLiked().size();

        boolean contains = false;
        for (Game g : user.getGamesLiked()) {
            if (g.getId() == game.getId()) {
                contains = true;
                break;
            }
        }

        if (!contains) {
            throw new IllegalArgumentException("El juego no está en tus favoritos");
        }

        user.getGamesLiked().remove(game);
        game.getUsersLiked().remove(user);
        userRepo.save(user);
    }
}

