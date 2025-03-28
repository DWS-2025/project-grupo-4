package com.casino.grupo4_dws.casinoweb.managers;

import com.casino.grupo4_dws.casinoweb.model.Bet;
import com.casino.grupo4_dws.casinoweb.model.Game;
import com.casino.grupo4_dws.casinoweb.model.User;
import com.casino.grupo4_dws.casinoweb.repos.BetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Random;

@Service
public class BetManager {

    @Autowired
    private BetRepository betRepo;

    public Bet playBet(Game gamePlayed, User player, int amount) {
        if (amount < gamePlayed.getMinInput()) {
            throw new IllegalArgumentException("La apuesta debe de ser mayor o igual a " + gamePlayed.getMinInput());
        }
        if (player.getMoney() < gamePlayed.getMinInput()) {
            throw new IllegalArgumentException("No tienes dinero suficiente para apostar");
        }
        if (player.getMoney() < amount) {
            throw new IllegalArgumentException("No tienes tanto dinero flipao");
        }
        if (amount <= 0) {
            throw new IllegalArgumentException("No puedes apostar en blanco");
        }


        Bet bet = new Bet();
        bet.setAmount(amount);
        bet.setUserPlayer(player);
        bet.setGame(gamePlayed);
        boolean win = playGame(bet);
        if (win) {
            int revenue = amount * gamePlayed.getMultiplier();
            bet.setRevenue(revenue);
            player.setMoney(player.getMoney() + revenue);
            bet.setStatus(true);
            betRepo.save(bet);
        } else {
            bet.setRevenue(0);
            player.setMoney(player.getMoney() - amount);
            bet.setStatus(false);
            betRepo.save(bet);
        }

        if (player.getBetHistory() == null) {
            player.setBetHistory(new ArrayList<>());
        }
        player.getBetHistory().add(bet);
        return bet;

    }

    private boolean playGame(Bet activeBet) {
        Random rand = new Random();
        int randomValue = rand.nextInt(100) + 1;
        return randomValue <= activeBet.GetGame().getChance();
    }

    public void Save(Bet bet) {
        betRepo.save(bet);
    }

}
