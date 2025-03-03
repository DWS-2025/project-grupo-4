package com.casino.grupo4_dws.casinoweb.model;

public class Prize {
    private String title;
    private int price;
    private String description;
    private String image;
    private int id;

    public Prize() {
    }

    public Prize(String title, int price, String description, String image, int id) {
        this.title = title;
        this.price = price;
        this.description = description;
        this.image = image;
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getPrice() {
        return price;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getImage() {
        return image;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
