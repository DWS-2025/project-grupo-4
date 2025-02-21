package com.casino.grupo4_dws.casinoweb.model;

public class Bet {
    private int amount;
    private int revenue = 0;
    private String user;
    private int date;
    private boolean status;
    private int gameID;


    public int GetAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int GetRevenue() { return revenue;}

    public void setRevenue(int revenue) {this.revenue = revenue;}

    public String GetUser() {return user;}

    public void setUser(String user) {this.user = user;}

    public int GetDate() {return date;}

    public void setDate(int date) {this.date = date;}

    public boolean GetStatus() {return status;}

    public void setStatus(boolean status) {this.status = status;}

    public int GetGameID() {return gameID;}

    public void setGameID(int gameID) {this.gameID = gameID;}

}