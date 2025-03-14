package com.casino.grupo4_dws.casinoweb.repos;

import com.casino.grupo4_dws.casinoweb.model.Prize;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PrizeRepository extends JpaRepository<Prize, Long> {
    @Override
    <S extends Prize> S save(S entity);

    @Override
    <S extends Prize> List<S> findAll(Example<S> example);
}
