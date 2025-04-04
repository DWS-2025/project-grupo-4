package com.casino.grupo4_dws.casinoweb.repos;

import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;
import com.casino.grupo4_dws.casinoweb.model.Bet;

import java.util.List;

public interface BetRepository extends JpaRepository<Bet, Long> {
    @Override
    <S extends Bet> S save(S entity);

    @Override
    <S extends Bet> List<S> findAll(Example<S> example);

}
