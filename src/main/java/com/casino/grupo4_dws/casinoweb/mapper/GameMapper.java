package com.casino.grupo4_dws.casinoweb.mapper;

import com.casino.grupo4_dws.casinoweb.dto.GameDTO;
import com.casino.grupo4_dws.casinoweb.model.Game;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface GameMapper {
    @Mapping(target = "minInput", source = "minInput")
    @Mapping(target = "multiplier", source = "multiplier")
    GameDTO toDTO(Game game);

    @Mapping(target = "image", source = "image")
    @Mapping(target = "chance", constant = "50")
    @Mapping(target = "usersLiked", ignore = true)
    Game toEntity(GameDTO gameDTO);

    List<GameDTO> toDTOList(List<Game> games);

    List<Game> toEntityList(List<GameDTO> gameDTOs);
}