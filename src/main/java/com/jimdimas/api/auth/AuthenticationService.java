package com.jimdimas.api.auth;

import com.jimdimas.api.config.JWTService;
import com.jimdimas.api.email.ApplicationEmailService;
import com.jimdimas.api.exception.BadRequestException;
import com.jimdimas.api.exception.ConflictException;
import com.jimdimas.api.exception.UnauthorizedException;
import com.jimdimas.api.token.TokenService;
import com.jimdimas.api.user.Role;
import com.jimdimas.api.user.User;
import com.jimdimas.api.user.UserRepository;
import com.jimdimas.api.util.UtilService;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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
    private final TokenService tokenService;

    public String register(User user) throws MessagingException, ConflictException {
        Optional<User> userEmailExists = userRepository.findUserByEmail(user.getEmail());
        Optional<User> userUsernameExists = userRepository.findUserByUsername(user.getUsername());
        if (userEmailExists.isPresent()){
            throw new ConflictException("Email is used");
        }
        if (userUsernameExists.isPresent()){
            throw new ConflictException("Username is used");
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

    public String login(User user, HttpServletResponse response) throws UnauthorizedException {
        Optional<User> userExists = userRepository.findUserByUsername(user.getUsername());
        User checkUserEnabled = userExists.get();
        if (!checkUserEnabled.isEnabled()){ //If user has not verified his email , he is denied access
            throw new UnauthorizedException("Verify email to login.");
        }

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        user.getUsername(),
                        user.getPassword()
                )
        );

        String accessToken = jwtService.generateAccessToken(checkUserEnabled);
        Cookie accessCookie = new Cookie("accessToken",accessToken);
        Cookie refreshCookie = new Cookie("refreshToken", tokenService.getRefreshToken(checkUserEnabled));
        accessCookie.setPath("/");
        refreshCookie.setPath("/");
        response.addCookie(accessCookie);
        response.addCookie(refreshCookie);
        return accessToken;
    }

    public String verifyEmail(String email, String token) throws BadRequestException {
        Optional<User> userExists = userRepository.findUserByEmail(email);
        if (!userExists.isPresent()){
            throw new BadRequestException("Failed email verification");
        }
        User user = userExists.get();
        if (!user.getVerificationToken().equals(token)){
            throw new BadRequestException("Failed email verification");
        }
        user.setVerificationToken("");
        userRepository.save(user);
        return "Email verification was succesfull";
    }

    public String forgotPassword(User user) throws MessagingException, BadRequestException {
        Optional<User> userExists = userRepository.findUserByUsername(user.getUsername());
        if (!userExists.isPresent()){
            throw new BadRequestException("Forgot password process failed");
        }
        User existingUser = userExists.get();
        if (!existingUser.getEmail().equals(user.getEmail())){
            throw new BadRequestException("Forgot password process failed");
        }
        existingUser.setPasswordToken(utilService.getSecureRandomToken(64));
        existingUser.setPasswordTokenExpirationDate(LocalDateTime.now().plusHours(1));
        emailService.sendChangePasswordMail(existingUser.getEmail(),existingUser.getPasswordToken());
        userRepository.save(existingUser);
        return "Check your email to change the password";
    }

    public String resetPassword(User user) throws BadRequestException {
        Optional<User> userExists = userRepository.findUserByEmail(user.getEmail());
        if (!userExists.isPresent()){
            throw new BadRequestException("Reset password process failed");
        }
        User existingUser = userExists.get();
        if (!existingUser.getPasswordToken().equals(user.getPasswordToken()) ||
                !existingUser.getPasswordTokenExpirationDate().isAfter(LocalDateTime.now())){
            throw new BadRequestException("Reset password process failed");
        }
        existingUser.setPasswordToken("");
        existingUser.setPasswordTokenExpirationDate(null);
        existingUser.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(existingUser);

        return "Password reset was successful";
    }
}
