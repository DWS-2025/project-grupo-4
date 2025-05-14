
package com.casino.grupo4_dws.casinoweb.apis;

import com.casino.grupo4_dws.casinoweb.dto.GameDTO;
import com.casino.grupo4_dws.casinoweb.managers.GameManager;
import com.casino.grupo4_dws.casinoweb.managers.UserManager;
import com.casino.grupo4_dws.casinoweb.model.User;
import com.casino.grupo4_dws.casinoweb.dto.UserDTO;
import com.casino.grupo4_dws.casinoweb.mapper.UserMapper;
import com.casino.grupo4_dws.casinoweb.repos.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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

    @GetMapping("")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<UserDTO> users = userManager.getUserList();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUser(@PathVariable int id) {
        Optional<UserDTO> user = userManager.findById(id);
        return user.map(u -> ResponseEntity.ok(u))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("")
    public ResponseEntity<UserDTO> createUser(@RequestBody UserDTO userDTO) {
        try {
            UserDTO user = userDTO;
            userManager.save(userMapper.toEntity(user));
            return ResponseEntity.status(HttpStatus.CREATED).body(user);
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
        try {
            if (userManager.findById(id).isPresent()) {
                userManager.deleteUser(id);
                return ResponseEntity.ok().build();
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{idUser}/favorite/{idGame}")
    public ResponseEntity<UserDTO> addFavorite(@PathVariable int idUser, @PathVariable int idGame) {
        userManager.setFav(userManager.findById(idUser).get(), gameManager.getGameById(idGame).get());
        return ResponseEntity.ok(userManager.findById(idUser).get());
    }

    @DeleteMapping("/{idUser}/favorite/{idGame}")
    public ResponseEntity<UserDTO> removeFavorite(@PathVariable int idUser, @PathVariable int idGame) {
        userManager.deleteFav(userManager.findById(idUser).get(), gameManager.getGameById(idGame).get());
        return ResponseEntity.ok(userManager.findById(idUser).get());
    }
}
