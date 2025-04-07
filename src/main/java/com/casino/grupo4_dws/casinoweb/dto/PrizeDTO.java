package com.casino.grupo4_dws.casinoweb.dto;

import java.sql.Blob;

public class PrizeDTO {
    private int id;
    private String title;
    private String description;
    private int price;
    private Blob image;

    public PrizeDTO() {}

    public PrizeDTO(int id, String title, String description, int price, Blob image) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.price = price;
        this.image = image;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
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

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public void setImage(Blob image) {
        this.image = image;
    }

    public Blob getImage() {
        return image;
    }
}