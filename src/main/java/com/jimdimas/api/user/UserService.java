package com.jimdimas.api.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor    //lombok annotation to create required constructors automatically
public class UserService {

    private final UserRepository userRepository;

    @GetMapping
    public List<User> getUsers(){
        return userRepository.findAll();
    }

    @PostMapping
    public void addUser(User user){
        Optional<User> userExists = userRepository.findUserByEmail(user.getEmail());
        if (userExists.isPresent()){
            throw new IllegalStateException("Email already taken");
        }
        user.setRole(Role.USER);
        userRepository.save(user);
    }
}
