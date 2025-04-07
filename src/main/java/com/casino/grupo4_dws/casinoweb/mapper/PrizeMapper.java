package com.casino.grupo4_dws.casinoweb.mapper;

import com.casino.grupo4_dws.casinoweb.dto.PrizeDTO;
import com.casino.grupo4_dws.casinoweb.model.Prize;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PrizeMapper {
    @Mapping(target = "id", source = "id")
    @Mapping(target = "title", source = "title")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "price", source = "price")
    PrizeDTO toDTO(Prize prize);

    @Mapping(target = "image", ignore = true)
    @Mapping(target = "owner", ignore = true)
    Prize toEntity(PrizeDTO prizeDTO);

    List<PrizeDTO> toDTOList(List<Prize> prizes);

    List<Prize> toEntityList(List<PrizeDTO> prizeDTOs);
}