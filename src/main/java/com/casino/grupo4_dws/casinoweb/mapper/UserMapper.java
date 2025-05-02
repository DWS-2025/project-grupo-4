package com.casino.grupo4_dws.casinoweb.mapper;// ... existing imports ...
import com.casino.grupo4_dws.casinoweb.dto.UserDTO;
import com.casino.grupo4_dws.casinoweb.model.Bet;
import com.casino.grupo4_dws.casinoweb.model.Game;
import com.casino.grupo4_dws.casinoweb.model.Prize;
import com.casino.grupo4_dws.casinoweb.model.User;
import com.casino.grupo4_dws.casinoweb.repos.GameRepository;
import com.casino.grupo4_dws.casinoweb.repos.PrizeRepository;
import com.casino.grupo4_dws.casinoweb.repos.BetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public abstract class UserMapper {

    @Autowired
    protected GameRepository gameRepository;

    @Autowired
    protected PrizeRepository prizeRepository;

    @Autowired
    protected BetRepository betRepository;

    @Mapping(target = "gamesLiked", source = "gamesLiked", qualifiedByName = "gamesToIds")
    @Mapping(target = "inventory", source = "inventory", qualifiedByName = "prizesToIds")
    @Mapping(target = "betHistory", source = "betHistory", qualifiedByName = "betsToIds")
    public abstract UserDTO toDTO(User user);

    @Mapping(target = "gamesLiked", source = "gamesLiked", qualifiedByName = "idsToGames")
    @Mapping(target = "inventory", source = "inventory", qualifiedByName = "idsToPrizes")
    @Mapping(target = "betHistory", source = "betHistory", qualifiedByName = "idsToBets")
    public abstract User toEntity(UserDTO userDTO);

    public abstract List<UserDTO> toDTOList(List<User> users);

    public abstract List<User> toEntityList(List<UserDTO> userDTOs);

    // --- Methods for mapping ---

    @Named("gamesToIds")
    public List<Integer> gamesToIds(List<Game> games) {
        if (games == null) return null;
        return games.stream().map(Game::getId).collect(Collectors.toList());
    }

    @Named("idsToGames")
    public List<Game> idsToGames(List<Integer> ids) {
        if (ids == null) return null;
        return gameRepository.findAllById(ids.stream().mapToLong(id -> id).boxed().collect(Collectors.toList()));
    }

    @Named("prizesToIds")
    public List<Integer> prizesToIds(List<Prize> prizes) {
        if (prizes == null) return null;
        return prizes.stream().map(Prize::getId).collect(Collectors.toList());
    }

    @Named("idsToPrizes")
    public List<Prize> idsToPrizes(List<Integer> ids) {
        if (ids == null) return null;
        return prizeRepository.findAllById(ids.stream().mapToLong(id -> id).boxed().collect(Collectors.toList()));
    }

    @Named("betsToIds")
    public List<Long> betsToIds(List<Bet> bets) {
        if (bets == null) return null;
        return bets.stream().map(Bet::getId).collect(Collectors.toList());
    }

    @Named("idsToBets")
    public List<Bet> idsToBets(List<Long> ids) {
        if (ids == null) return null;
        return betRepository.findAllById(ids);
    }
}