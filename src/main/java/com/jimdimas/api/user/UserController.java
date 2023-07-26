package com.jimdimas.api.user;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping({"/api/v1/user"})
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public List<User> getUsers(){
        return userService.getUsers();
    }

    @GetMapping(path="{userId}")
    public Optional<User> getUserById(@PathVariable Integer userId){ return userService.getUserById(userId); }

    @PostMapping
    public void addUser(@RequestBody User user){
        userService.addUser(user);
    }

    @DeleteMapping(path="{userId}")
    public void deleteUser(@PathVariable Integer userId){ userService.deleteUser(userId); }

    @PutMapping(path="{userId}")
    public void updateUser(@PathVariable Integer userId,@RequestBody User user){ userService.updateUser(userId,user); }
}
