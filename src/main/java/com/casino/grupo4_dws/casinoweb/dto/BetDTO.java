package com.casino.grupo4_dws.casinoweb.dto;

import com.casino.grupo4_dws.casinoweb.dto.GameDTO;
import com.casino.grupo4_dws.casinoweb.dto.UserDTO;

public record BetDTO(long id,
                     int amount,
                     int revenue,
                     boolean status,
                     boolean show,
                     int game,
                     int user,
                     String gameTitle) {
}