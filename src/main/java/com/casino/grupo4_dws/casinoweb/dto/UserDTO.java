package com.casino.grupo4_dws.casinoweb.dto;

import java.util.ArrayList;
import java.util.List;

public class UserDTO {
    private int id;
    private String userName;
    private String password;
    private boolean isadmin;
    private int money;
    private List<Integer> gamesLiked;
    private List<Integer> inventory;
    private List<Long> betHistory;
}