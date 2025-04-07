package com.casino.grupo4_dws.casinoweb.managers;

import com.casino.grupo4_dws.casinoweb.dto.*;
import com.casino.grupo4_dws.casinoweb.mapper.GameMapper;
import com.casino.grupo4_dws.casinoweb.mapper.PrizeMapper;
import com.casino.grupo4_dws.casinoweb.mapper.UserMapper;
import com.casino.grupo4_dws.casinoweb.model.Game;
import com.casino.grupo4_dws.casinoweb.model.Prize;
import com.casino.grupo4_dws.casinoweb.model.User;
import com.casino.grupo4_dws.casinoweb.repos.GameRepository;
import com.casino.grupo4_dws.casinoweb.repos.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserManager {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private GameRepository gameRepo;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private GameMapper gameMapper;

    @Autowired
    private PrizeMapper prizeMapper;

    public List<UserDTO> getUserList() {
        return userRepo.findAll().stream()
                .map(user -> userMapper.toDTO(user))
                .collect(Collectors.toList());
    }

    public UserDTO save(UserDTO userDTO) {
        User user = userMapper.toEntity(userDTO);
        User savedUser = userRepo.save(user);
        return userMapper.toDTO(savedUser);
    }

    public void postConstruct(){
        UserDTO user1 = new UserDTO();
        user1.setUsername("gigandres");
        user1.setPassword("1234");
        user1.setBalance(5000);
        user1.setAdmin(true);
        save(user1);

        UserDTO user2 = new UserDTO();
        user2.setUsername("ralpi");
        user2.setPassword("qwerty");
        user2.setBalance(10000);
        user2.setAdmin(true);
        save(user2);

        UserDTO user3 = new UserDTO();
        user3.setUsername("user");
        user3.setPassword("aaaaa");
        user3.setBalance(500);
        user3.setAdmin(false);
        save(user3);

        UserDTO user4 = new UserDTO();
        user4.setUsername("userprize");
        user4.setPassword("1234");
        user4.setBalance(1500);
        user4.setAdmin(false);
        save(user4);

        UserDTO user5 = new UserDTO();
        user5.setUsername("saultj");
        user5.setPassword("abc");
        user5.setBalance(2147483647);
        user5.setAdmin(true);
        save(user5);
    }

    public Optional<UserDTO> findById(int id) {
        return userRepo.getUserById(id)
                .map(user -> userMapper.toDTO(user));
    }

    public Optional<UserDTO> findByUsername(String username) {
        return userRepo.getUserByUserName(username)
                .map(user -> userMapper.toDTO(user));
    }

    public void deleteUser(long id){
        if (!userRepo.existsById(id)) {
            throw new IllegalArgumentException("El usuario introducido no existe");
        }
        userRepo.deleteById(id);
    }

    public boolean isUserCorrect(String username, String password) {
        return userRepo.getUserByUserName(username)
                .map(user -> user.getPassword().equals(password))
                .orElse(false);
    }

    public UserDTO saveUser(String username, String password) {
        if (userRepo.getUserByUserName(username).isPresent()) {
            throw new IllegalArgumentException("Ya existe un usuario con ese nombre");
        }

        UserDTO newUserDTO = new UserDTO();
        newUserDTO.setUsername(username);
        newUserDTO.setPassword(password);
        newUserDTO.setBalance(500);
        newUserDTO.setAdmin(false);

        return save(newUserDTO);
    }

    @Transactional
    public PrizeDTO buyPrize(PrizeDTO prizeDTO, UserDTO userDTO) {
        User user = userMapper.toEntity(userDTO);
        Prize prize = prizeMapper.toEntity(prizeDTO);

        if (user.getInventory() == null) {
            user.setInventory(new ArrayList<>());
        }
        if (user.getInventory().contains(prize)) {
            throw new IllegalArgumentException("Ya tienes el producto comprado");
        }
        if (prize.getPrice() > user.getMoney()) {
            throw new IllegalArgumentException("No tienes suficiente dinero");
        }
        if (prize.getOwner() != null && !prize.getOwner().equals(user)) {
            throw new IllegalArgumentException("Este premio ya pertenece a otro usuario");
        }

        user.getInventory().add(prize);
        prize.setOwner(user);
        user.setMoney(user.getMoney() - prize.getPrice());

        try {
            User savedUser = userRepo.save(user);
            return prizeMapper.toDTO(prize);
        } catch (Exception e) {
            throw new RuntimeException("Error al guardar el usuario despues de comprar");
        }
    }

    public UserDTO getUserWithGamesLiked(int id) {
        User user = userRepo.findByIdWithGamesLiked(id);
        UserDTO userGamesDTO = userMapper.toDTO(user);
        userGamesDTO.setFavoriteGames(user.getGamesLiked().stream()
                .map(game -> gameMapper.toDTO(game))
                .collect(Collectors.toList()));
        return userGamesDTO;
    }

    public UserDTO findByIdMeta(int id) {
        return getUserWithGamesLiked(id);
    }

    public UserDTO setFav(UserDTO userDTO, GameDTO gameDTO) {
        User user = userMapper.toEntity(userDTO);
        Game game = gameMapper.toEntity(gameDTO);

        if (game.getUsersLiked() == null) {
            game.setUsersLiked(new ArrayList<>());
        }
        if (user.getGamesLiked() == null) {
            user.setGamesLiked(new ArrayList<>());
        }
        if (user.getGamesLiked().contains(game)) {
            throw new IllegalArgumentException("Ya tienes el juego en favoritos");
        }

        user.getGamesLiked().add(game);
        User savedUser = userRepo.save(user);

        return userMapper.toDTO(savedUser);
    }

    @Transactional
    public void deleteFav(UserDTO userDTO, GameDTO gameDTO) {
        User user = userMapper.toEntity(userDTO);
        Game game = gameMapper.toEntity(gameDTO);

        // Get fresh entities from database to ensure collections are properly loaded
        user = userRepo.findById((long) user.getId())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        game = gameRepo.findById((long) game.getId())
                .orElseThrow(() -> new IllegalArgumentException("Juego no encontrado"));


        if (!user.getGamesLiked().contains(game)) {
            throw new IllegalArgumentException("El juego no está en tus favoritos");
        }

        user.getGamesLiked().remove(game);
        game.getUsersLiked().remove(user);

        // Save both entities
        userRepo.save(user);
        gameRepo.save(game);

        // Update the DTO to reflect changes
        userDTO.setFavoriteGames(user.getGamesLiked().stream()
                .map(gameMapper::toDTO)
                .collect(Collectors.toList()));
    }
}