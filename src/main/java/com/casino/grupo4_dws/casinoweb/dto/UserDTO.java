package com.casino.grupo4_dws.casinoweb.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.List;

public record UserDTO(int id,
                      String userName,
                      @JsonIgnore
                      String password,
                      Boolean isadmin,
                      Integer money,
                      String documentPath,
                      List<Integer> gamesLiked,
                      List<Integer> inventory,
                      List<Long> betHistory) {
}