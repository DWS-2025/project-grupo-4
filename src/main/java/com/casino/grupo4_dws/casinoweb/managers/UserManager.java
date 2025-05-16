package com.casino.grupo4_dws.casinoweb.managers;

import com.casino.grupo4_dws.casinoweb.dto.*;
import com.casino.grupo4_dws.casinoweb.mapper.GameMapper;
import com.casino.grupo4_dws.casinoweb.mapper.PrizeMapper;
import com.casino.grupo4_dws.casinoweb.mapper.UserMapper;
import com.casino.grupo4_dws.casinoweb.model.Game;
import com.casino.grupo4_dws.casinoweb.model.Prize;
import com.casino.grupo4_dws.casinoweb.model.User;
import com.casino.grupo4_dws.casinoweb.repos.GameRepository;
import com.casino.grupo4_dws.casinoweb.repos.PrizeRepository;
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

    @Autowired
    private PrizeRepository prizeRepo;

    public List<UserDTO> getUserList() {
        return userRepo.findAll().stream()
                .map(user -> userMapper.toDTO(user))
                .collect(Collectors.toList());
    }

    public UserDTO save(User user) {
        User savedUser = userRepo.save(user);
        return userMapper.toDTO(savedUser);
    }

    public void postConstruct() {
        User user1 = new User();
        user1.setUserName("gigandres");
        user1.setPassword("1234");
        user1.setMoney(5000);
        user1.setIsadmin(true);
        userRepo.save(user1);

        User user2 = new User();
        user2.setUserName("ralpi");
        user2.setPassword("qwerty");
        user2.setMoney(10000);
        user2.setIsadmin(true);
        userRepo.save(user2);

        User user3 = new User();
        user3.setUserName("user");
        user3.setPassword("aaaaa");
        user3.setMoney(500);
        user3.setIsadmin(false);
        userRepo.save(user3);

        User user4 = new User();
        user4.setUserName("userprize");
        user4.setPassword("1234");
        user4.setMoney(1500);
        user4.setIsadmin(false);
        userRepo.save(user4);

        User user5 = new User();
        user5.setUserName("saultj");
        user5.setPassword("abc");
        user5.setMoney(2147483647);
        user5.setIsadmin(true);
        userRepo.save(user5);
    }

    public Optional<UserDTO> findById(int id) {
        return userRepo.getONEUserById(id)
                .map(user -> userMapper.toDTO(user));
    }

    public Optional<UserDTO> findDTOByUsername(String username) {
        return userRepo.getUserByUserName(username)
                .map(user -> userMapper.toDTO(user));
    }

    public Optional<User> findByUsername(String username) {
        return userRepo.getUserByUserName(username);
    }

    public void deleteUser(long id) {
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

    public User saveUser(String username, String password) {
        if (userRepo.getUserByUserName(username).isPresent()) {
            throw new IllegalArgumentException("Ya existe un usuario con ese nombre");
        }

        User newUser = new User();
        newUser.setUserName(username);
        newUser.setPassword(password);
        newUser.setMoney(500);
        newUser.setIsadmin(false);

        return userRepo.save(newUser);
    }

    @Transactional
    public UserDTO buyPrize(PrizeDTO prizedto, UserDTO userdto) {
        Optional<User> userOp = userRepo.getONEUserById(userMapper.toEntity(userdto).getId());
        if (userOp.isEmpty()) {
            throw new IllegalArgumentException("El usuario introducido no existe");
        }
        Optional<Prize> prizeOp = prizeRepo.findPrizeById(prizeMapper.toEntity(prizedto).getId());
        if (prizeOp.isEmpty()) {
            throw new IllegalArgumentException("El prize introducido no existe");
        }
        Prize prize = prizeOp.get();
        User user = userOp.get();

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
            userRepo.save(user);
            prizeRepo.save(prize);
            return userMapper.toDTO(user);
        } catch (Exception e) {
            throw new RuntimeException("Error al guardar el usuario despues de comprar");
        }
    }

    public void setFav(UserDTO userdto, GameDTO gamedto) {
        // Check user existence
        Optional<User> userOp = userRepo.getONEUserById(userMapper.toEntity(userdto).getId());
        if (userOp.isEmpty()) {
            throw new IllegalArgumentException("El usuario introducido no existe");
        }
        User user = userOp.get();
        // Check game existence
        Optional<Game> gameOp = gameRepo.findGameById(gameMapper.toEntity(gamedto).getId());
        if (gameOp.isEmpty()) {
            throw new IllegalArgumentException("El game introducido no existe");
        }
        Game game = gameOp.get();

        if (game.getUsersLiked() == null) {
            game.setUsersLiked(new ArrayList<>());
        }
        if (user.getGamesLiked() == null) {
            user.setGamesLiked(new ArrayList<>());
        }
        if (user.getGamesLiked().contains(game) || game.getUsersLiked().contains(user)) {
            throw new IllegalArgumentException("Ya tienes el juego en favoritos");
        } else {
            user.getGamesLiked().add(game);
            userRepo.save(user);
            gameRepo.save(game);
        }
    }

    @Transactional
    public void deleteFav(UserDTO userdto, GameDTO gamedto) {
        // Check for game existance
        Optional<Game> gameOp = gameRepo.findGameById(gameMapper.toEntity(gamedto).getId());
        if (gameOp.isEmpty()) {
            throw new IllegalArgumentException("El usuario introducido no existe");
        }
        Game game = gameOp.get();
        // Check for game existance
        Optional<User> userOp = userRepo.getONEUserById(userMapper.toEntity(userdto).getId());
        if (userOp.isEmpty()) {
            throw new IllegalArgumentException("El usuario introducido no existe");
        }
        User user = userOp.get();
        // Check game-user favorite relation
        if (!user.getGamesLiked().contains(game)) {
            throw new IllegalArgumentException("El juego no está en tus favoritos");
        }
        // Remove relation
        user.getGamesLiked().remove(game);
        game.getUsersLiked().remove(user);
        // Save both entities back
        userRepo.save(user);
        gameRepo.save(game);
    }

    public List<GameDTO> getFavGames(UserDTO userdto) {
        Optional<User> userOp = userRepo.getONEUserById(userMapper.toEntity(userdto).getId());
        if (userOp.isEmpty()) {
            throw new IllegalArgumentException("El usuario introducido no existe");
        }
        User user = userOp.get();
        if (user.getGamesLiked() == null) {
            user.setGamesLiked(new ArrayList<>());
        }
        return gameMapper.toDTOList(user.getGamesLiked());
    }

    public boolean likesGame(int idUser, int idGame){
        Optional<User> user = userRepo.getONEUserById(idUser);
        if(user.isEmpty()){
            throw new IllegalArgumentException("El usuario introducido no existe");
        }
        else{
            List<Game> gameList = user.get().getGamesLiked();
            Optional<Game> favGame = gameRepo.findGameById(idGame);
            if(favGame.isEmpty()){
                throw new IllegalArgumentException("El game introducido no existe");
            }
            else{
                if(gameList.contains(favGame.get())){
                    return true;
                }
                else{
                    return false;
                }
            }
        }
    }

    public void deleteUser(UserDTO userdto, UserDTO activeUserdto) {
        Optional<User> userOp = userRepo.getONEUserById(userMapper.toEntity(userdto).getId());
        if (userOp.isEmpty()) {
            throw new IllegalArgumentException("El usuario introducido no existe");
        }
        userRepo.delete(userOp.get());
    }

    public boolean isAdmin(UserDTO userdto) {
        Optional<User> userOp = userRepo.getONEUserById(userMapper.toEntity(userdto).getId());
        if (userOp.isEmpty()) {
            throw new IllegalArgumentException("El usuario introducido no existe");
        }
        User user = userOp.get();
        return user.getIsadmin();
    }

    public void updateUser(UserDTO userDTO, int id){
        User modifiedUser = userRepo.getONEUserById(id).get();
        User modification = userMapper.toEntity(userDTO);

        if(modification.getUserName() != null){
            modifiedUser.setUserName(modification.getUserName());
        }
        if(modification.getPassword() != null){
            modifiedUser.setPassword(modification.getPassword());
        }
        userRepo.save(modifiedUser);
    }
    public void updateUserAdmin(UserDTO userDTO, int id){
        User modifiedUser = userRepo.getONEUserById(id).get();
        User modification = userMapper.toEntity(userDTO);

        if(modification.getUserName() != null){
            modifiedUser.setUserName(modification.getUserName());
        }
        if(modification.getPassword() != null){
            modifiedUser.setPassword(modification.getPassword());
        }
            modifiedUser.setMoney(modification.getMoney());
            modifiedUser.setIsadmin(modification.getIsadmin());

        userRepo.save(modifiedUser);
    }

    public boolean isOwner(UserDTO userdto, PrizeDTO prizedto) {
        Optional<User> userOp = userRepo.getONEUserById(userMapper.toEntity(userdto).getId());
        if (userOp.isEmpty()) {
            throw new IllegalArgumentException("El usuario introducido no existe");
        }
        User user = userOp.get();
        Optional<Prize> prizeOp = prizeRepo.getPrizeById(prizeMapper.toEntity(prizedto).getId());
        if (prizeOp.isEmpty()) {
            throw new IllegalArgumentException("El prize introducido no existe");
        }
        if(user.getInventory() == null){
            user.setInventory(new ArrayList<>());
        }
        Prize prize = prizeOp.get();
        return user.getInventory().contains(prize);
    }
}