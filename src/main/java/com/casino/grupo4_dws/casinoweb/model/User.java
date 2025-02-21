package com.casino.grupo4_dws.casinoweb.model;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

@Component
@SessionScope
public class User {
    private int userName;
    private int password;
    private int money;
    private boolean isadmin;

    public int getUserName() {
        return userName;
    }
    public void setUserName(int userName) {
        this.userName = userName;
    }
    public int getPassword() {
        return password;
    }
    public void setPassword(int password) {
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
}
