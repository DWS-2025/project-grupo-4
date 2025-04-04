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
    private int userID;
    @ManyToOne
    @JoinColumn(name = "game_id")
    private Game game;
    private boolean status;


    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int getRevenue() {
        return revenue;
    }

    public void setRevenue(int revenue) {
        this.revenue = revenue;
    }

    public int getUser() {
        return userID;
    }

    public void setUserPlayer(int activeUser) {
        this.userID = activeUser;
    }

    public int getDate() {
        return date;
    }

    public void setDate(int date) {
        this.date = date;
    }

    public boolean getStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game gamePlayed) {
        this.game = gamePlayed;
    }
    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }

}