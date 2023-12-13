package com.jimdimas.api.auth;

import com.jimdimas.api.exception.BadRequestException;
import com.jimdimas.api.exception.ConflictException;
import com.jimdimas.api.exception.UnauthorizedException;
import com.jimdimas.api.user.User;
import com.jimdimas.api.util.JsonResponse;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletResponse;
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
    public ResponseEntity<JsonResponse> register(@RequestBody User user) throws MessagingException, ConflictException {
        return ResponseEntity.ok(authenticationService.register(user));
    }

    @PostMapping("/login")
    public ResponseEntity<JsonResponse> login(@RequestBody User user,
                                        HttpServletResponse response) throws UnauthorizedException {
        return ResponseEntity.ok(authenticationService.login(user,response));
    }

    @GetMapping("/verifyEmail")
    public ResponseEntity<JsonResponse> verifyEmail(
            @RequestParam(name = "user") String username,
            @RequestParam(name="verificationToken") String token) throws BadRequestException {
        return ResponseEntity.ok(authenticationService.verifyEmail(username,token));
    }

    @PostMapping("/forgotPassword")
    public ResponseEntity<JsonResponse> forgotPassword(@RequestBody User user) throws MessagingException, BadRequestException {    //provided email and username
        return ResponseEntity.ok(authenticationService.forgotPassword(user));
    }

    @PostMapping("/resetPassword")
    public ResponseEntity<JsonResponse> resetPassword(@RequestBody User user) throws BadRequestException {    //serialize User's password,password token and email fields
        return ResponseEntity.ok(authenticationService.resetPassword(user));
    }
}
