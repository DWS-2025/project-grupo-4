package com.casino.grupo4_dws.casinoweb.dto;

import java.sql.Blob;
import java.util.ArrayList;
import java.util.List;

public class GameDTO {
    private long id;
    private String title;
    private String description;
    private int minInput;
    private int multiplier;
    private int chance;
    private List<UserDTO> usersLiked;
    private Blob image;

    public GameDTO() {}

    public GameDTO(int id, String title, String description, int minInput, int multiplier, int chance) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.minInput = minInput;
        this.multiplier = multiplier;
        this.chance = chance;
        this.usersLiked = new ArrayList<>();

    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getMinInput() {
        return minInput;
    }

    public void setMinInput(int minInput) {
        this.minInput = minInput;
    }

    public int getChance(){
        return chance;
    }

    public void setChance(int chance){
        this.chance = chance;
    }

    public int getMultiplier() {
        return multiplier;
    }

    public void setMultiplier(int maxInput) {
        this.multiplier = maxInput;
    }

    public List<UserDTO> getUsersLiked() {
        return usersLiked;
    }
    public void setUsersLiked(List<UserDTO> usersLiked) {
        this.usersLiked = usersLiked;
    }
    public Blob getImage() {
        return image;
    }
    public void setImage(Blob image) {
        this.image = image;
    }
}