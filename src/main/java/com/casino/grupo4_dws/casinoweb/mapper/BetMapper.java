package com.casino.grupo4_dws.casinoweb.mapper;

import com.casino.grupo4_dws.casinoweb.dto.BetDTO;
import com.casino.grupo4_dws.casinoweb.dto.GameDTO;
import com.casino.grupo4_dws.casinoweb.dto.UserDTO;
import com.casino.grupo4_dws.casinoweb.model.Bet;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface BetMapper {
    @Mapping(target = "id", expression = "java((int)bet.getId())")
    @Mapping(target = "status", source = "status")
    BetDTO toDTO(Bet bet);
    @Mapping(target = "id", expression = "java((long)betDTO.getId())")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "revenue", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "game", ignore = true)
    @Mapping(target = "gameTitle", ignore = true)
    @Mapping(target = "show", constant = "true")
    @Mapping(target = "amount",source = "amount")
    Bet toEntity(BetDTO betDTO);

    List<BetDTO> toDTOList(List<Bet> bets);
    List<Bet> toEntityList(List<BetDTO> betDTOs);
}