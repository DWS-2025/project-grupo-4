package com.casino.grupo4_dws.casinoweb.model;

import java.util.Random;

public class Game {
    private int id;
    private String title;
    private String description;
    private String image;
    private int chance;
    private int minInput;
    private int multiplier;

    public Game(int id, String title, String description, String image, int chance, int minInput, int multiplier) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.image = image;
        this.chance = chance;
        this.minInput = minInput;
        this.multiplier = multiplier;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getImage() {
        return image;
    }

    public int getChance() {
        return chance;
    }

    public int getMinInput() {
        return minInput;
    }

    public int getMultiplier() {
        return multiplier;
    }

}