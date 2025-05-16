
package com.casino.grupo4_dws.casinoweb.apis;

import com.casino.grupo4_dws.casinoweb.dto.GameDTO;
import com.casino.grupo4_dws.casinoweb.dto.PrizeDTO;
import com.casino.grupo4_dws.casinoweb.managers.GameManager;
import com.casino.grupo4_dws.casinoweb.managers.JWTManager;
import com.casino.grupo4_dws.casinoweb.managers.PrizeManager;
import com.casino.grupo4_dws.casinoweb.managers.UserManager;
import com.casino.grupo4_dws.casinoweb.mapper.PrizeMapper;
import com.casino.grupo4_dws.casinoweb.model.User;
import com.casino.grupo4_dws.casinoweb.dto.UserDTO;
import com.casino.grupo4_dws.casinoweb.mapper.UserMapper;
import com.casino.grupo4_dws.casinoweb.repos.UserRepository;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/user")
public class SessionAPI {

    @Autowired
    private UserManager userManager;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private GameManager gameManager;
    @Autowired
    private PrizeManager prizeManager;
    @Autowired
    private JWTManager jwtManager;

    @GetMapping("")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<UserDTO> users = userManager.getUserList();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUser(@PathVariable int id) {
        if(userManager.findById(id).isPresent()){
            return ResponseEntity.ok(userManager.findById(id).get());
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("")
    public ResponseEntity<UserDTO> createUser(@RequestBody UserDTO userDTO) {
        try {
            userManager.save(userMapper.toEntity(userDTO));
            return ResponseEntity.status(HttpStatus.CREATED).body(userDTO);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable int id, @RequestBody UserDTO userDTO) {
        try {
            userManager.updateUser(userDTO, id);
            return ResponseEntity.ok(userDTO);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<User> deleteUser(@PathVariable int id) {
        if (userManager.findById(id).isPresent()) {
            userManager.deleteUser(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/{idUser}/favorite")
    public ResponseEntity<List<GameDTO>> getFavoriteGames(@PathVariable int idUser) {
        if(userManager.findById(idUser).isPresent()){
            UserDTO userDTO = userManager.findById(idUser).get();
            List<GameDTO> favList = userManager.getFavGames(userDTO);
            return ResponseEntity.ok(favList);
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/{idUser}/favorite/{idGame}")
    public ResponseEntity<UserDTO> addFavorite(@PathVariable int idUser, @PathVariable int idGame) {
        if(userManager.findById(idUser).isPresent() && gameManager.getGameById(idGame).isPresent()) {
            userManager.setFav(userManager.findById(idUser).get(), gameManager.getGameById(idGame).get());
            return ResponseEntity.ok(userManager.findById(idUser).get());
        }
        return ResponseEntity.notFound().build();

    }

    @DeleteMapping("/{idUser}/favorite/{idGame}")
    public ResponseEntity<UserDTO> removeFavorite(@PathVariable int idUser, @PathVariable int idGame) {
        if (userManager.findById(idUser).isPresent() && gameManager.getGameById(idGame).isPresent()) {
            userManager.deleteFav(userManager.findById(idUser).get(), gameManager.getGameById(idGame).get());
            return ResponseEntity.ok(userManager.findById(idUser).get());
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/{idUser}/buy/{idPrize}")
    public ResponseEntity<UserDTO> buy(@PathVariable int idUser, @PathVariable int idPrize) {
        Optional<UserDTO> userDTO = userManager.findById(idUser);
        Optional<PrizeDTO> prizeDTO = prizeManager.findById(idPrize);

        if(userDTO.isPresent() && prizeDTO.isPresent()){
            userManager.buyPrize(prizeDTO.get(), userDTO.get());
            UserDTO updatedUser = userManager.findById(idUser).get();
            return ResponseEntity.ok(updatedUser);
        } else
            return ResponseEntity.notFound().build();
    }

    /*
    Methods Register and Login accept two user & pass strings because
    UserDTO does not contain a password field, so using a DTO would make
    data transfer harder, and also allow user to modify their params (money, isAdmin...)
     */

    @PostMapping("/register")
    public ResponseEntity<UserDTO> registerUser(@RequestBody Map<String, String> data) {
        String username = data.get("username");
        String password = data.get("password");
        return ResponseEntity.status(HttpStatus.CREATED).body(userManager.saveUser(username, password));

    }

    @PostMapping("/login")
    public ResponseEntity<String> loginUser(@RequestBody Map<String, String> data) {
        String username = data.get("username");
        String password = data.get("password");

        if(userManager.isUserCorrect(username, password)){
            UserDTO userDTO = userMapper.toDTO(userManager.findByUsername(username).get());
            return ResponseEntity.ok(jwtManager.generateToken(userDTO));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @GetMapping("/testtoken")
    public ResponseEntity<Claims> testToken(@RequestHeader("Authorization") String jwtToken) {
        String token = jwtToken.replace("Bearer ", "");
        return ResponseEntity.ok(jwtManager.verifyToken(token));
    }

}
