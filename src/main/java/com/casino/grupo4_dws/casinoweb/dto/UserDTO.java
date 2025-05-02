package com.casino.grupo4_dws.casinoweb.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.ArrayList;
import java.util.List;

public record UserDTO(int id,
                      String userName,
                      @JsonIgnore
                      String password,
                      boolean isadmin,
                      int money,
                      List<Integer> gamesLiked,
                      List<Integer> inventory,
                      List<Long> betHistory) {
}