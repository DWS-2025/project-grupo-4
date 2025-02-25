package com.casino.grupo4_dws.casinoweb.model;

public class Prize {
    private String title;
    private int price;
    private String description;
    private String image;  // Corrección en el nombre (minúscula)
    private int id;

    // 🔹 Constructor vacío (necesario para Jackson)
    public Prize() {}

    // 🔹 Constructor con parámetros
    public Prize(String title, int price, String description, String image, int id) {
        this.title = title;
        this.price = price;
        this.description = description;
        this.image = image;
        this.id = id;
    }

    // 🔹 Métodos setters
    public void setTitle(String title) { this.title = title; }
    public void setPrice(int price) { this.price = price; }
    public void setDescription(String description) { this.description = description; }
    public void setImage(String image) { this.image = image; }
    public void setId(int id) { this.id = id; }

    // 🔹 Métodos getters con nombres correctos
    public String getTitle() { return title; }
    public int getPrice() { return price; }
    public String getDescription() { return description; }
    public String getImage() { return image; }  // No convertir a int
    public int getId() { return id; }
}
