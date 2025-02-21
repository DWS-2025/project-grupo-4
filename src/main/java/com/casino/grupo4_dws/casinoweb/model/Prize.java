package com.casino.grupo4_dws.casinoweb.model;

public class Prize {
    private String title;
    private int price;
    private String description;
    private String Image;

    public void setTitle(String title) {
        this.title = title;
    }
    public void setPrice(int price) {
        this.price = price;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public void setImage(String image) {
        Image = image;
    }

    public String GetTitle() {
        return title;
    }
    public int GetPrice() {
        return price;
    }
    public String GetDescription() {
        return description;
    }
    public int GetImage() {
        return Integer.parseInt(Image);
    }
}


