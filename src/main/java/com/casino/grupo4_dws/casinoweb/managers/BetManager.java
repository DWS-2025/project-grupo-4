package com.casino.grupo4_dws.casinoweb.managers;

import com.casino.grupo4_dws.casinoweb.dto.BetDTO;
import com.casino.grupo4_dws.casinoweb.dto.GameDTO;
import com.casino.grupo4_dws.casinoweb.dto.UserDTO;
import com.casino.grupo4_dws.casinoweb.mapper.BetMapper;
import com.casino.grupo4_dws.casinoweb.mapper.GameMapper;
import com.casino.grupo4_dws.casinoweb.mapper.UserMapper;
import com.casino.grupo4_dws.casinoweb.model.Bet;
import com.casino.grupo4_dws.casinoweb.model.Game;
import com.casino.grupo4_dws.casinoweb.model.User;
import com.casino.grupo4_dws.casinoweb.repos.BetRepository;
import com.casino.grupo4_dws.casinoweb.repos.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Random;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BetManager {

    @Autowired
    private BetRepository betRepo;

    @Autowired
    private BetMapper betMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private GameMapper gameMapper;

    @Transactional
    public BetDTO playBet(GameDTO gameDTO, UserDTO userDTO, int amount) {
        Game game = gameMapper.toEntity(gameDTO);
        User player = userRepo.findById((long)userDTO.getId())
                .orElseThrow(() -> new RuntimeException("User not found")); // 🔄 aquí el cambio

        if (amount < game.getMinInput()) {
            throw new IllegalArgumentException("La apuesta debe de ser mayor o igual a " + game.getMinInput());
        }
        if (player.getMoney() < game.getMinInput()) {
            throw new IllegalArgumentException("No tienes dinero suficiente para apostar");
        }
        if (player.getMoney() < amount) {
            throw new IllegalArgumentException("No tienes tanto dinero");
        }
        if (amount <= 0) {
            throw new IllegalArgumentException("No puedes apostar en blanco");
        }

        Bet bet = new Bet();
        bet.setShow(true);
        bet.setAmount(amount);
        bet.setUserPlayer(player);
        bet.setGame(game);
        bet.setUserPlayer(player);
        boolean win = playGame(bet);

        if (win) {
            int revenue = amount * game.getMultiplier();
            bet.setRevenue(revenue);
            player.setMoney(player.getMoney() + revenue);
            bet.setStatus(true);
        } else {
            bet.setRevenue(0);
            player.setMoney(player.getMoney() - amount);
            bet.setStatus(false);
        }

        Bet savedBet = betRepo.save(bet);

        // Ensure the user's bet history is initialized
        if (player.getBetHistory() == null) {
            player.setBetHistory(new ArrayList<>());
        }

        // Update user's bet history
        player.getBetHistory().add(savedBet);

        // Save the updated user
        userRepo.save(player);

        return betMapper.toDTO(savedBet);
    }


    private boolean playGame(Bet activeBet) {
        Random rand = new Random();
        int randomValue = rand.nextInt(100) + 1;
        return randomValue <= activeBet.getGame().getChance();
    }

    public BetDTO save(BetDTO betDTO) {
        Bet bet = betMapper.toEntity(betDTO);
        Bet savedBet = betRepo.save(bet);
        return betMapper.toDTO(savedBet);
    }

    public List<BetDTO> findAll() {
        return betRepo.findAll().stream()
                .map(bet -> betMapper.toDTO(bet))
                .collect(Collectors.toList());
    }

    public Optional<BetDTO> findById(long id) {
        return betRepo.findById(id)
                .map(bet -> betMapper.toDTO(bet));
    }

    public void delete(long id) {
        betRepo.findById(id).ifPresent(bet -> {
            bet.setShow(false);
            betRepo.save(bet);
        });
    }
    /*
    public void notShow(BetDTO betDTO) {
        Bet bet = betMapper.toEntity(betDTO);
        bet.setShow(false);
        betRepo.save(bet);
    }

    public void notShowById(long id) {
        betRepo.findById(id).ifPresent(bet -> {
            bet.setShow(false);
            betRepo.save(bet);
        });
    }*/
}
