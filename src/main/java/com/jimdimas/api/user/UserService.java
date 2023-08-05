package com.jimdimas.api.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

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

    @GetMapping
    public Optional<User> getUserById(Integer userId) { return userRepository.findById(userId); }

    @GetMapping
    public Optional<User> getUserByUsername(String username){  return userRepository.findUserByUsername(username);}

    @PostMapping
    public void addUser(User user){
        Optional<User> userEmailExists = userRepository.findUserByEmail(user.getEmail());
        Optional<User> userUsernameExists = userRepository.findUserByUsername(user.getUsername());
        if (userEmailExists.isPresent()){
            throw new IllegalStateException("Email already taken");
        }
        if (userUsernameExists.isPresent()){
            throw new IllegalStateException("Username already taken");
        }
        user.setRole(Role.USER);
        userRepository.save(user);
    }

    @DeleteMapping
    public void deleteUser(Integer userId){
        Optional<User> userExists = userRepository.findById(userId);
        if (!userExists.isPresent()){
            throw new IllegalStateException("User with id : "+userId.toString()+" does not exist");
        }
        userRepository.deleteById(userId);
    }

    @PutMapping
    public void updateUser(Integer userId,User user){
        Optional<User> userExists = userRepository.findById(userId);
        if (!userExists.isPresent()){
            throw new IllegalStateException("User with id "+userId.toString()+" does not exist");
        }
        User updatedUser = userExists.get();

        if (user.getEmail()!=null && !user.getEmail().isBlank()){
            Optional<User> userEmailExists = userRepository.findUserByEmail(user.getEmail());
            if (userEmailExists.isPresent()){
                throw new IllegalStateException("Cannot update email to "+user.getEmail()+" , it's already taken.");
            }
            updatedUser.setEmail(user.getEmail());
        }

        if (user.getUsername()!=null && !user.getLastName().isBlank()){
            Optional<User> userUsernameExists = userRepository.findUserByUsername(user.getUsername());

            if (userUsernameExists.isPresent()){
                throw new IllegalStateException("Cannot update username to "+user.getUsername()+" , it's already taken.");
            }
            updatedUser.setUsername(user.getUsername());
        }

        if (user.getDob()!=null){
            updatedUser.setDob(user.getDob());
        }

        if (user.getFirstName()!=null){
            updatedUser.setFirstName(user.getFirstName());
        }

        if (user.getLastName()!=null){
            updatedUser.setLastName(user.getLastName());
        }

        if (user.getPassword()!=null) {
            updatedUser.setPassword(user.getPassword());
        }

        userRepository.save(updatedUser);
    }
}
