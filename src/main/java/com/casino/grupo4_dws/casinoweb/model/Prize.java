package com.casino.grupo4_dws.casinoweb.model;

import jakarta.persistence.*;
import java.sql.Blob;

import java.sql.Blob;

@Entity
public class Prize {
    private String title;
    private int price;
    private String description;
    @Lob    // Image defined as Blob and Lob tag to operate with database
    private Blob image;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    @ManyToOne
    private User owner;

    public Prize() {
    }

    public Prize(String title, int price, String description, Blob image) {
        this.title = title;
        this.price = price;
        this.description = description;
        this.image = image;
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

    public void setImage(Blob image) {
        this.image = image;
    }

    public Blob getImage() {
        return image;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }
    public User getOwner() {
        return owner;
    }
}
