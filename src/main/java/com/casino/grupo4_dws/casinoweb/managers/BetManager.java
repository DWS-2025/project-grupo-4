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
        User player = userMapper.toEntity(userDTO);

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
        bet.setUser(player);
        bet.setGame(game);
        boolean win = playGame(bet);

        if (win) {
            int revenue;
            try {
                revenue = Math.multiplyExact(amount, game.getMultiplier());
            } catch (ArithmeticException e) {
                throw new IllegalArgumentException("La apuesta es demasiado alta y causa un overflow.");
            }

            bet.setRevenue(revenue);

            try {
                int newBalance = Math.addExact(player.getMoney(), revenue);
                player.setMoney(newBalance);
            } catch (ArithmeticException e) {
                throw new IllegalArgumentException("El balance resultante causaría un overflow.");
            }

            bet.setStatus(true);
        } else {
            bet.setRevenue(0);
            player.setMoney(player.getMoney() - amount);
            bet.setStatus(false);
        }


        bet = betRepo.save(bet);
        userRepo.save(player);

        return betMapper.toDTO(bet);
    }


    private boolean playGame(Bet activeBet) {
        Random rand = new Random();
        int randomValue = rand.nextInt(100) + 1;
        return randomValue <= activeBet.getGame().getChance();
    }

    public BetDTO save(Bet bet) {
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

    public boolean getStatusDTO(BetDTO betdto) {
        return betMapper.toEntity(betdto).getStatus();
    }

    public void notShow(BetDTO betDTO, UserDTO userdto) {
        Bet bet = betMapper.toEntity(betDTO);
        User user = userMapper.toEntity(userdto);
        if (user.getId() == bet.getUser().getId()) {
            bet.setShow(false);
            betRepo.save(bet);
        }
    }

    //This method isn't functional, we made it because Michel said all entities must have delete and create methods.
    //However, our database model don't allow to delete a Bet without deleting a User.
    public void deleteBet(BetDTO betDTO, UserDTO userDTO) {
        Optional<Bet> betOp = betRepo.getONEBetById(betMapper.toEntity(betDTO).getId());
        if (betOp.isEmpty()) {
            throw new IllegalArgumentException("No se puede eliminar la bet");
        }
        Bet bet = betOp.get();
        Optional<User> userOp = userRepo.getONEUserById(userMapper.toEntity(userDTO).getId());
        if (userOp.isEmpty()) {
            throw new IllegalArgumentException("No se puede eliminar la bet");
        }
        User user = userOp.get();
        if (user.getBetHistory().contains(bet)) {
            betRepo.delete(bet);
        } else if (user.getIsadmin()) {
            betRepo.delete(bet);
        } else {
            throw new IllegalArgumentException("No se puede eliminar la bet");
        }
    }

    public void updateBet(BetDTO betDTO, int id) {
        var possibleBet = findById(id);

        if (possibleBet.isPresent()) {
            Bet bet = betMapper.toEntity(betDTO);
            bet.setId(id);
            betRepo.save(bet);
        } else {
            throw new IllegalArgumentException("EL id de apuesta no existe");
        }
    }

    //Alternative to deleting a bet, just making it not show under a User profile
    public void hideBet(BetDTO betDTO, UserDTO userDTO) {
        Optional<Bet> betOp = betRepo.getONEBetById(betMapper.toEntity(betDTO).getId());
        if (betOp.isEmpty()) {
            throw new IllegalArgumentException("No se puede eliminar la bet");
        }
        Bet bet = betOp.get();
        Optional<User> userOp = userRepo.getONEUserById(userMapper.toEntity(userDTO).getId());
        if (userOp.isEmpty()) {
            throw new IllegalArgumentException("No se puede eliminar la bet");
        }
        User user = userOp.get();
        if (user.getBetHistory().contains(bet)) {
            bet.setShow(false);
            betRepo.save(bet);
        } else if (user.getIsadmin()) {
            bet.setShow(false);
            betRepo.save(bet);
        } else {
            throw new IllegalArgumentException("No se puede eliminar la bet");
        }
    }

}
