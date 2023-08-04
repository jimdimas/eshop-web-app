package com.jimdimas.api.auth;

import com.jimdimas.api.config.JWTService;
import com.jimdimas.api.user.Role;
import com.jimdimas.api.user.User;
import com.jimdimas.api.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JWTService jwtService;
    private final AuthenticationManager authenticationManager;

    public String register(User user){
        Optional<User> userExists = userRepository.findUserByEmail(user.getEmail());
        if (userExists.isPresent()){
            throw new IllegalStateException("Email is used");
        }
        User endUser = User.builder()
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getUsername())
                .password(passwordEncoder.encode(user.getPassword()))
                .dob(user.getDob())
                .role(Role.USER)
                .build();
        userRepository.save(endUser);
        return jwtService.generateToken(endUser);
    }

    public String login(User user){
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        user.getUsername(),
                        user.getPassword()
                )
        );
        return jwtService.generateToken(user);
    }
}
