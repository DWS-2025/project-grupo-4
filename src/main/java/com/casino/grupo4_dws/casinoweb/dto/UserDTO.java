package com.casino.grupo4_dws.casinoweb.dto;

import java.util.ArrayList;
import java.util.List;

public class UserDTO {
    private int id;
    private String username;
    private String password;
    private boolean admin;
    private int balance;
    private List<GameDTO> gamesLiked;
    private List<PrizeDTO> inventory;
    private List<BetDTO> betHistory;

    public UserDTO() {
    }

    public UserDTO(int id, String username, int balance) {
        this.id = id;
        this.username = username;
        this.balance = balance;
        this.gamesLiked = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<GameDTO> getFavoriteGames() {
        return gamesLiked;
    }

    public void setFavoriteGames(List<GameDTO> favoriteGames) {
        this.gamesLiked = favoriteGames;
    }

    public List<PrizeDTO> getInventory() {
        return inventory;
    }

    public void setInventory(List<PrizeDTO> inventory) {
        this.inventory = inventory;
    }

    public List<BetDTO> getBetHistory() {
        return betHistory;
    }

    public void setBetHistory(List<BetDTO> betHistory) {
        this.betHistory = betHistory;
    }
}