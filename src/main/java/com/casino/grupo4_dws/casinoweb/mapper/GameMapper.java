package com.casino.grupo4_dws.casinoweb.mapper;

import com.casino.grupo4_dws.casinoweb.dto.GameDTO;
import com.casino.grupo4_dws.casinoweb.model.Game;
import com.casino.grupo4_dws.casinoweb.model.User;
import com.casino.grupo4_dws.casinoweb.repos.UserRepository;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public abstract class GameMapper {
    @Autowired
    protected UserRepository userRepository;

    @Mapping(target = "usersLiked", source = "usersLiked", qualifiedByName = "usersToIds")
    public abstract GameDTO toDTO(Game game);

    @Mapping(target = "usersLiked", source = "usersLiked", qualifiedByName = "idsToUsers")
    public abstract Game toEntity(GameDTO gameDTO);

    public abstract List<GameDTO> toDTOList(List<Game> games);

    public abstract List<Game> toEntityList(List<GameDTO> gameDTOs);

    @Named("usersToIds")
    public List<Integer> usersToIds(List<User> users) {
        if (users == null) return null;
        return users.stream().map(User::getId).collect(Collectors.toList());
    }

    @Named("idsToUsers")
    public List<User> idsToUsers(List<Integer> ids) {
        if (ids == null) return null;
        return userRepository.findAllById(ids.stream().mapToLong(id -> id).boxed().collect(Collectors.toList()));
    }

}