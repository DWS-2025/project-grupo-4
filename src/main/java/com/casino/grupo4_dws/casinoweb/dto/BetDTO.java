package com.casino.grupo4_dws.casinoweb.dto;

public class BetDTO {
    private int id;
    private int amount;
    private boolean result;

    public BetDTO() {}

    public BetDTO(int id, int amount, boolean result) {
        this.id = id;
        this.amount = amount;
        this.result = result;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }
}