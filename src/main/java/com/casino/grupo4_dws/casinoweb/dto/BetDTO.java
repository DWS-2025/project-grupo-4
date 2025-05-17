package com.casino.grupo4_dws.casinoweb.dto;

public record BetDTO(long id,
                     int amount,
                     int revenue,
                     boolean status,
                     boolean show,
                     int game,
                     int user,
                     String gameTitle) {
}