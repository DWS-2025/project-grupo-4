package com.casino.grupo4_dws.casinoweb.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;

import java.util.List;
import java.util.Random;
@Entity
public class Game {
    @Id
    private int id;
    private String title;
    private String description;
    private String image;
    private int chance;
    private int minInput;
    private int multiplier;
    @ManyToMany
    private List<User> usersLiked;

    public Game() {
    }

    public Game(int id, String title, String description, String image, int chance, int minInput, int multiplier) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.image = image;
        this.chance = chance;
        this.minInput = minInput;
        this.multiplier = multiplier;
    }

    public List<User> getUsersLiked() {
        return usersLiked;
    }

    public void setUsersLiked(List<User> usersLiked) {
        this.usersLiked = usersLiked;
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

    public void setId(int id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setChance(int chance) {
        this.chance = chance;
    }

    public void setMinInput(int minInput) {
        this.minInput = minInput;
    }

    public void setMultiplier(int multiplier) {
        this.multiplier = multiplier;
    }

}