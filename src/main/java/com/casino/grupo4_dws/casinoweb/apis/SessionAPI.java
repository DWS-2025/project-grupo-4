package com.casino.grupo4_dws.casinoweb.apis;

import com.casino.grupo4_dws.casinoweb.managers.UserManager;
import com.casino.grupo4_dws.casinoweb.model.User;
import com.casino.grupo4_dws.casinoweb.dto.UserDTO;
import com.casino.grupo4_dws.casinoweb.mapper.UserMapper;
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

    @GetMapping("")
    public ResponseEntity<List<UserDTO>> getAllUsers(){
        List<User> users = userManager.getUserList();
        return ResponseEntity.ok(userMapper.toDTOList(users));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUser(@PathVariable int id){
        Optional<User> user = userManager.findById(id);
        return user.map(u -> ResponseEntity.ok(userMapper.toDTO(u)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("")
    public ResponseEntity<UserDTO> createUser(@RequestBody UserDTO userDTO){
        try {
            User user = userMapper.toEntity(userDTO);
            userManager.save(user);
            return ResponseEntity.status(HttpStatus.CREATED).body(userMapper.toDTO(user));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable int id, @RequestBody UserDTO userDTO){
        try {
            if (userManager.findById(id).isPresent()) {
                User user = userMapper.toEntity(userDTO);
                user.setId(id);
                userManager.save(user);
                return ResponseEntity.ok(userMapper.toDTO(user));
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<User> deleteUser(@PathVariable int id){
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
}
