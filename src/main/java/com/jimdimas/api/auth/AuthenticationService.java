package com.jimdimas.api.auth;

import com.jimdimas.api.config.JWTService;
import com.jimdimas.api.email.ApplicationEmailService;
import com.jimdimas.api.user.Role;
import com.jimdimas.api.user.User;
import com.jimdimas.api.user.UserRepository;
import com.jimdimas.api.util.UtilService;
import jakarta.mail.MessagingException;
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
    private final UtilService utilService;
    private final ApplicationEmailService emailService;

    public String register(User user) throws MessagingException {
        Optional<User> userEmailExists = userRepository.findUserByEmail(user.getEmail());
        Optional<User> userUsernameExists = userRepository.findUserByUsername(user.getUsername());
        if (userEmailExists.isPresent()){
            throw new IllegalStateException("Email is used");
        }
        if (userUsernameExists.isPresent()){
            throw new IllegalStateException("Username is used");
        }
        User endUser = User.builder()
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .username(user.getUsername())
                .email(user.getEmail())
                .password(passwordEncoder.encode(user.getPassword()))
                .verificationToken(utilService.getSecureRandomToken(32))
                .dob(user.getDob())
                .role(Role.USER)
                .build();
        emailService.sendVerificationMail(endUser.getEmail(), endUser.getVerificationToken());
        userRepository.save(endUser);
        return "Verify email to end register process";
    }

    public String login(User user){
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        user.getUsername(),
                        user.getPassword()
                )
        );
        Optional<User> userExists = userRepository.findUserByUsername(user.getUsername());
        User checkUserEnabled = userExists.get();
        if (!checkUserEnabled.isEnabled()){ //If user has not verified his email , he is denied access
            throw new IllegalStateException("Something went wrong,try again");
        }
        return jwtService.generateToken(user);
    }

    public String verifyEmail(String email, String token) {
        Optional<User> userExists = userRepository.findUserByEmail(email);
        if (!userExists.isPresent()){
            throw new IllegalStateException("Failed email verification");
        }
        User user = userExists.get();
        if (!user.getVerificationToken().equals(token)){
            throw new IllegalStateException("Failed email verification");
        }
        user.setVerificationToken("");
        userRepository.save(user);
        return "Email verification was succesfull";
    }
}
