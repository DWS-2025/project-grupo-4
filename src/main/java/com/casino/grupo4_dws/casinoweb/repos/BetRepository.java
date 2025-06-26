package com.casino.grupo4_dws.casinoweb.repos;

import com.casino.grupo4_dws.casinoweb.model.Game;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;
import com.casino.grupo4_dws.casinoweb.model.Bet;

import java.util.List;
import java.util.Optional;

public interface BetRepository extends JpaRepository<Bet, Long> {
    @Override
    <S extends Bet> S save(S entity);

    @Override
    <S extends Bet> List<S> findAll(Example<S> example);

    Optional<Bet> getONEBetById(long id);

    List<Bet> findByGame(Game game);
}
