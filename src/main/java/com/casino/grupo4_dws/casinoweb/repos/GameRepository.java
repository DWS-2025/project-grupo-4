package com.casino.grupo4_dws.casinoweb.repos;

import com.casino.grupo4_dws.casinoweb.model.Game;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GameRepository extends JpaRepository<Game, Long> {
    @Override
    <S extends Game> S save(S entity);

    @Override
    <S extends Game> List<S> findAll(Example<S> example);
}
