package com.jimdimas.api.user;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping({"/api/v1/user"})
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public List<User> getUsers(@RequestAttribute(name = "user") User requestingUser){
        return userService.getUsers(requestingUser);
    }

    @GetMapping(path="{username}")
    public Optional<User> getUserById(
            @RequestAttribute(name = "user") User requestingUser,
             @PathVariable String username ){
        return userService.getUserByUsername(requestingUser,username); }

    @PostMapping
    public void addUser(
            @RequestAttribute(name="user") User requestingUser,
            @RequestBody User newUser ) throws MessagingException {
        userService.addUser(requestingUser,newUser);
    }

    @PutMapping
    public void updateUser(
            @RequestAttribute(name = "user") User requestingUser,
            @RequestParam(name="username") String username,
            @RequestBody User updatedUser ){
        userService.updateUser(requestingUser,username,updatedUser); }

    @PutMapping("/changePassword")
    public void changePassword(
            @RequestAttribute(name="user") User user,
            @RequestBody Map<String,String> passwordSet){
        userService.changePassword(user,passwordSet);
    }

    @PutMapping("/changeEmail")
    public ResponseEntity<String> changeEmail(
            @RequestAttribute("user") User user,
            @RequestBody Map<String,String> passwordAndEmail) throws MessagingException {
        return ResponseEntity.ok(userService.changeEmail(user,passwordAndEmail));
    }
}
