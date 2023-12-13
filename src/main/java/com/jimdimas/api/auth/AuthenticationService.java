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
import com.jimdimas.api.util.JsonResponse;
import com.jimdimas.api.util.UtilService;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
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

    public JsonResponse register(User user) throws MessagingException, ConflictException {
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
        emailService.sendVerificationMail(endUser, endUser.getVerificationToken());
        userRepository.save(endUser);
        return JsonResponse.builder().message("Verify email to end register process").build();
    }

    public JsonResponse login(User user, HttpServletResponse response) throws UnauthorizedException {
        Optional<User> userExists = userRepository.findUserByEmail(user.getEmail());
        if (!userExists.isPresent()){
            throw new UnauthorizedException("Invalid credentials provided");
        }
        User checkUserEnabled = userExists.get();
        if (!checkUserEnabled.isEnabled()){ //If user has not verified his email , he is denied access
            throw new UnauthorizedException("Verify email to login.");
        }

        User actualUser = userExists.get();
        String username = actualUser.getUsername();
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        username,
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
        return JsonResponse.builder().message("Successful login").build();
    }

    public JsonResponse verifyEmail(String username, String token) throws BadRequestException {
        Optional<User> userExists = userRepository.findUserByUsername(username);
        if (!userExists.isPresent()){
            throw new BadRequestException("Failed email verification");
        }
        User user = userExists.get();
        if (!user.getVerificationToken().equals(token)){
            throw new BadRequestException("Failed email verification");
        }
        user.setVerificationToken("");
        userRepository.save(user);
        return JsonResponse.builder().message("Email verification was succesfull").build();
    }

    public JsonResponse forgotPassword(User user) throws MessagingException, BadRequestException {
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
        emailService.sendChangePasswordMail(existingUser,existingUser.getPasswordToken());
        userRepository.save(existingUser);
        return JsonResponse.builder().message("Check your email to change the password").build();
    }

    public JsonResponse resetPassword(User user) throws BadRequestException {
        Optional<User> userExists = userRepository.findUserByUsername(user.getUsername());
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

        return JsonResponse.builder().message("Password reset was successful").build();
    }

    public JsonResponse logout(User user, HttpServletRequest request, HttpServletResponse response) throws UnauthorizedException {
        Optional<Cookie> refreshCookieExists = Arrays.stream(request.getCookies()).filter(cookie -> cookie.getName().equals("refreshToken")).findFirst();
        if (!refreshCookieExists.isPresent()){
            throw new UnauthorizedException("Unauthorized to access this route");
        }
        Cookie oldRefreshCookie = refreshCookieExists.get();
        Cookie accessCookie = new Cookie("accessToken",null);
        Cookie refreshCookie = new Cookie("refreshToken",null);
        accessCookie.setMaxAge(0);
        refreshCookie.setMaxAge(0);
        accessCookie.setPath("/");
        refreshCookie.setPath("/");
        response.addCookie(accessCookie);
        response.addCookie(refreshCookie);
        tokenService.deleteToken(user,oldRefreshCookie.getValue());
        return JsonResponse.builder().message("Logout successful").build();
    }
}
