package com.casino.grupo4_dws.casinoweb.model;

import jakarta.persistence.*;

@Entity
public class Bet {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private int amount;
    private int revenue = 0;
    private int date;
    @ManyToOne
    private User userPlayer;
    @ManyToOne
    @JoinColumn(name = "game_id")
    private Game game;
    private boolean status;


    public int GetAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int GetRevenue() {
        return revenue;
    }

    public void setRevenue(int revenue) {
        this.revenue = revenue;
    }

    public User GetUser() {
        return userPlayer;
    }

    public void setUserPlayer(User activeUser) {
        this.userPlayer = activeUser;
    }

    public int GetDate() {
        return date;
    }

    public void setDate(int date) {
        this.date = date;
    }

    public boolean GetStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public Game GetGame() {
        return game;
    }

    public void setGame(Game gamePlayed) {
        this.game = gamePlayed;
    }
    public long GetId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }

}