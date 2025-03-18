package com.casino.grupo4_dws.casinoweb.managers;

import com.casino.grupo4_dws.casinoweb.model.User;
import com.casino.grupo4_dws.casinoweb.repos.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserManager {

    @Autowired
    private UserRepository userRepo;
    public List<User> findAll() {
        return userRepo.findAll();
    }
    public User save(User user) {
        return userRepo.save(user);
    }
    public void postConstruct(){
        userRepo.save(new User("gigandres","1234",5000,true));
        userRepo.save(new User("ralpi","qwerty",10000,true));
        userRepo.save(new User("user","aaaaa",500,false));
    }
    public Optional<User> findByUsername(String username) {
        return userRepo.getUserByUserName(username);
    }
    public boolean isUserCorrect(String username, String password) {
        Optional<User> user = userRepo.getUserByUserName(username);
        if (user.isPresent()) {
            if (user.get().getPassword().equals(password)) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }
    public void saveUser(String username, String password) {
        Optional<User> user = userRepo.getUserByUserName(username);
        if (user.isPresent()) {
            throw new IllegalArgumentException("Ya existe un usuario con ese nombre");
        }
        userRepo.save(new User(username,password,500,false));
    }
}
