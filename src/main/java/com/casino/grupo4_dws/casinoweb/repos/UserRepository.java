package com.casino.grupo4_dws.casinoweb.repos;

import com.casino.grupo4_dws.casinoweb.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {
    @Override
    <S extends User> S save(S entity);

    @Override
    <S extends User> List<S> findAll(Example<S> example);

    Optional <User> getUserByUserName(String userName);

}
