package com.casino.grupo4_dws.casinoweb.model;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    private String userName;
    private String password;
    @Column(nullable = false)
    private int money;
    private boolean isadmin;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "owner", orphanRemoval = true)
    private List<Prize> Inventory;

    @ManyToMany
    private List<Game> gamesLiked;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    private List<Bet> betHistory;

    public User() {
    }

    public User(String userName, String password, int money, boolean isadmin) {
        this.userName = userName;
        this.password = password;
        this.money = money;
        this.isadmin = isadmin;
    }

    public List<Bet> getBetHistory() {
        return betHistory;
    }

    public void setBetHistory(List<Bet> betHistory) {
        this.betHistory = betHistory;
    }

    public List<Game> getGamesLiked() {
        return gamesLiked;
    }

    public void setGamesLiked(List<Game> gamesLiked) {
        this.gamesLiked = gamesLiked;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public boolean getIsadmin() {
        return isadmin;
    }

    public void setIsadmin(boolean isadmin) {
        this.isadmin = isadmin;
    }

    public List<Prize> getInventory() {
        return Inventory;
    }

    public void setInventory(List<Prize> Inventory) {
        this.Inventory = Inventory;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
