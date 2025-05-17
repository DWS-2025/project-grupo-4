package com.casino.grupo4_dws.casinoweb.mapper;

import com.casino.grupo4_dws.casinoweb.dto.BetDTO;
import com.casino.grupo4_dws.casinoweb.model.Bet;
import com.casino.grupo4_dws.casinoweb.model.Game;
import com.casino.grupo4_dws.casinoweb.model.User;
import com.casino.grupo4_dws.casinoweb.repos.GameRepository;
import com.casino.grupo4_dws.casinoweb.repos.UserRepository;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

@Mapper(componentModel = "spring")
public abstract class BetMapper {
    @Autowired
    protected GameRepository gameRepository;
    @Autowired
    protected UserRepository userRepository;

    @Mapping(target = "game", source = "game", qualifiedByName = "gamesToId")
    @Mapping(target = "user", source = "user", qualifiedByName = "userToId")
    public abstract BetDTO toDTO(Bet bet);

    @Mapping(target = "game", source = "game", qualifiedByName = "idToGame")
    @Mapping(target = "user", source = "user", qualifiedByName = "idToUser")
    public abstract Bet toEntity(BetDTO betDTO);

    public abstract List<BetDTO> toDTOList(List<Bet> bets);

    public abstract List<Bet> toEntityList(List<BetDTO> betDTOs);

    // --- Methods for mapping ---
    @Named("gamesToId")
    public int gamesToId(Game game) {
        if (game == null) return 0;
        return game.getId();
    }

    @Named("idToGame")
    public Game idToGame(int id) {
        if (id == 0) return null;
        Optional<Game> game = gameRepository.findGameById(id);
        if (!game.isPresent()) return null;
        return game.get();
    }

    @Named("userToId")
    public int userToId(User user) {
        if (user == null) return 0;
        return user.getId();
    }

    @Named("idToUser")
    public User idToUser(int id) {
        if (id == 0) return null;
        Optional<User> user = userRepository.findById((long) id);
        if (!user.isPresent()) return null;
        return user.get();
    }
}