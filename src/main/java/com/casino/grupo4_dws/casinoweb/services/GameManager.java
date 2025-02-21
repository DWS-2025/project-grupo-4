package com.casino.grupo4_dws.casinoweb.services;

import com.casino.grupo4_dws.casinoweb.model.Game;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class GameManager {

    List <Game> ListaJuegos;

    @PostConstruct
    private void InciarJuegos(){
        System.out.println("A");
    }
}
