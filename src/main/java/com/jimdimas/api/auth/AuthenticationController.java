package com.jimdimas.api.auth;

import com.jimdimas.api.user.User;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody User user) throws MessagingException {
        return ResponseEntity.ok(authenticationService.register(user));
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody User user){
        return ResponseEntity.ok(authenticationService.login(user));
    }

    @GetMapping("/verifyEmail")
    public ResponseEntity<String> verifyEmail(
            @RequestParam(name = "email") String email,
            @RequestParam(name="verificationToken") String token){
        return ResponseEntity.ok(authenticationService.verifyEmail(email,token));
    }

    @PostMapping("/forgotPassword")
    public ResponseEntity<String> forgotPassword(@RequestBody User user) throws MessagingException {    //provided email and username
        return ResponseEntity.ok(authenticationService.forgotPassword(user));
    }

    @PostMapping("/resetPassword")
    public ResponseEntity<String> resetPassword(@RequestBody User user){    //serialize User's password,password token and email fields
        return ResponseEntity.ok(authenticationService.resetPassword(user));
    }
}
