package com.casino.grupo4_dws.casinoweb.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name = "bet")
public class Bet {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private int amount;
    private int revenue = 0;
    @JsonIgnore
    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private User user;
    @JsonIgnore
    @ManyToOne(optional = true)
    @JoinColumn(name = "game_id")
    private Game game;
    private String gameTitle;
    private boolean status;
    @Column(name = "ver")
    private boolean show;

    public String getGameTitle() {
        return gameTitle;
    }

    public void setGameTitle(String gameTitle) {
        this.gameTitle = gameTitle;
    }

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

    public User getUser() {
        return user;
    }

    public int getUserId() {
        return user != null ? user.getId() : 0;
    }

    public String getUserName() {
        return user != null ? user.getUserName() : null;
    }

    public Integer getGameId() {
        return game != null ? game.getId() : null;
    }

    public void setUserPlayer(User activeUser) {
        this.user = activeUser;
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
        if (gamePlayed != null) {
            this.gameTitle = gamePlayed.getTitle();
        }
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public boolean isShow() {
        return show;
    }

    public void setShow(boolean show) {
        this.show = show;
    }

}