package com.jimdimas.api.user;

import com.jimdimas.api.exception.ConflictException;
import com.jimdimas.api.exception.NotFoundException;
import com.jimdimas.api.exception.UnauthorizedException;
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
    public List<User> getUsers(@RequestAttribute(name = "user") User requestingUser) throws UnauthorizedException {
        return userService.getUsers(requestingUser);
    }

    @GetMapping(path="{username}")
    public Optional<User> getUserById(@PathVariable(name="user") String username){
        return userService.getUserByUsername(username); }

    @PostMapping
    public void addUser(
            @RequestAttribute(name="user") User requestingUser,
            @RequestBody User newUser ) throws MessagingException, ConflictException, UnauthorizedException {
        userService.addUser(requestingUser,newUser);
    }

    @PutMapping
    public void updateUser(
            @RequestAttribute(name = "user") User requestingUser,
            @RequestParam(name="username") String username,
            @RequestBody User updatedUser ) throws UnauthorizedException, NotFoundException {
        userService.updateUser(requestingUser,username,updatedUser); }

    @PutMapping("/changeEmail")
    public ResponseEntity<String> changeEmail(
            @RequestAttribute("user") User user,
            @RequestBody Map<String,String> passwordAndEmail) throws MessagingException, ConflictException {
        return ResponseEntity.ok(userService.changeEmail(user,passwordAndEmail));
    }
}
