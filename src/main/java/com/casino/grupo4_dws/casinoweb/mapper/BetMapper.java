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
    BetDTO toDTO(Bet bet);

    Bet toEntity(BetDTO betDTO);

    List<BetDTO> toDTOList(List<Bet> bets);

    List<Bet> toEntityList(List<BetDTO> betDTOs);
}