package com.casino.grupo4_dws.casinoweb.model;

import org.yaml.snakeyaml.constructor.Constructor;

public class Bet {
    private int amount;


    public int GetAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}