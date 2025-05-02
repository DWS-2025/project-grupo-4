package com.casino.grupo4_dws.casinoweb.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.sql.Blob;
import java.util.ArrayList;
import java.util.List;

public record GameDTO(long id,
                      String title,
                      String description,
                      int minInput,
                      int multiplier,
                      int chance,
                      List<Integer> usersLiked,
                      @JsonIgnore
                      Blob image) {
}