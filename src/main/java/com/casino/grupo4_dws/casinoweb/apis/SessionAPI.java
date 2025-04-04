package com.casino.grupo4_dws.casinoweb.apis;

import com.casino.grupo4_dws.casinoweb.managers.UserManager;
import com.casino.grupo4_dws.casinoweb.model.User;
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

    @GetMapping("")
    public ResponseEntity<List<User>> getAllUsers(){
        List<User> users = userManager.getUserList();

        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUser(@PathVariable int id){
        Optional<User> user = userManager.findById(id);

        return user.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("")
    public ResponseEntity<User> createUser(@RequestBody User user){
        try {
            userManager.save(user);
            return ResponseEntity.status(HttpStatus.CREATED).body(user);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable int id, @RequestBody User user){
        try {
            if (userManager.findById(id).isPresent()) {
                user.setId(id);
                userManager.save(user);
                return ResponseEntity.ok(user);
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
