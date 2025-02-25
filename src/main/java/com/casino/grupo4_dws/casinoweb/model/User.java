package com.casino.grupo4_dws.casinoweb.model;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import java.util.List;

@Component
@SessionScope
public class User {
    private String userName;
    private String password;
    private int money;
    private boolean isadmin;
    private List <Prize> inventario;

    public String getUserName() {
        return userName;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public int getMoney() {
        return money;
    }
    public void setMoney(int money) {
        this.money = money;
    }
    public boolean isIsadmin() {
        return isadmin;
    }
    public void setIsadmin(boolean isadmin) {
        this.isadmin = isadmin;
    }
    public List<Prize> getInventario() {
        return inventario;
    }
    public void setInventario(List<Prize> inventario) {
        this.inventario = inventario;
    }
}
