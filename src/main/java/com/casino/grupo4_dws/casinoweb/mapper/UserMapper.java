package com.casino.grupo4_dws.casinoweb.mapper;

import com.casino.grupo4_dws.casinoweb.dto.UserDTO;
import com.casino.grupo4_dws.casinoweb.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "id", source = "id")
    @Mapping(target = "username", source = "userName")
    @Mapping(target = "balance", source = "money")
    @Mapping(target = "betHistory", source = "betHistory")
    UserDTO toDTO(User user);

    @Mapping(target = "userName", source = "username")
    @Mapping(target = "money", source = "balance")
    @Mapping(target = "password", source = "password")
    @Mapping(target = "isadmin", constant = "false")
    @Mapping(target = "inventory", ignore = true)
    @Mapping(target = "gamesLiked", ignore = true)
    @Mapping(target = "betHistory", source = "betHistory")
    User toEntity(UserDTO userDTO);

    List<UserDTO> toDTOList(List<User> users);

    List<User> toEntityList(List<UserDTO> userDTOs);
}