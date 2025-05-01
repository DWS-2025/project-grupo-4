package com.casino.grupo4_dws.casinoweb.mapper;

import com.casino.grupo4_dws.casinoweb.dto.GameDTO;
import com.casino.grupo4_dws.casinoweb.model.Game;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface GameMapper {
    GameDTO toDTO(Game game);

    Game toEntity(GameDTO gameDTO);

    List<GameDTO> toDTOList(List<Game> games);

    List<Game> toEntityList(List<GameDTO> gameDTOs);
}