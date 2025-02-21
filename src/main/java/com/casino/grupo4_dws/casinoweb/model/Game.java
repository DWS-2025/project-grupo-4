package com.casino.grupo4_dws.casinoweb.model;

import java.util.Random;

public class Game {
    private int id;
    private String title;
    private String description;
    private String image;
    private int chance;
    private int min_input;
    private int multiplier;

    public Game(int id, String title, String description, String image, int chance, int min_input, int multiplier) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.image = image;
        this.chance = chance;
        this.min_input = min_input;
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
    public int getMin_input() {
        return min_input;
    }

    public int getMultiplier() {
        return multiplier;
    }

    public int play(int input){
        // Genera un número aleatorio entre 0 y 100
        Random random = new Random();
        double numeroAleatorio = random.nextDouble() * 100;

        // Compara el número aleatorio con la probabilidad dada
        if (numeroAleatorio < chance) {
            return input * multiplier;
        } else {
            return 0;
        }
    }
}


