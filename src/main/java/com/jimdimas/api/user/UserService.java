package com.jimdimas.api.user;

import com.jimdimas.api.email.ApplicationEmailService;
import com.jimdimas.api.exception.ConflictException;
import com.jimdimas.api.exception.NotFoundException;
import com.jimdimas.api.exception.UnauthorizedException;
import com.jimdimas.api.util.UtilService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/* This is the internal user service , used by admins to view and manipulate user data or by an existing user to view,update or delete his profile */
@Service
@RequiredArgsConstructor    //lombok annotation to create required constructors automatically
public class UserService {

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final UtilService utilService;
    private final ApplicationEmailService emailService;

    @GetMapping
    public List<User> getUsers(User user) throws UnauthorizedException {
        if (!user.getRole().equals(Role.ADMIN)){
            throw new UnauthorizedException("Access not allowed");
        }

        return userRepository.findAll();
    }

    @GetMapping
    public Optional<User> getUserByUsername(User requestingUser,String username) throws UnauthorizedException {
        if (!requestingUser.getUsername().equals(username) && //a user can only view his profile
                !requestingUser.getRole().equals(Role.ADMIN)){
            throw new UnauthorizedException("Access not allowed");
        }

        return userRepository.findUserByUsername(username); }


    @PostMapping
    public void addUser(User existingUser,User user) throws MessagingException, UnauthorizedException, ConflictException {    //only admins can post users here,regular users need to use auth service
        if (!existingUser.getRole().equals(Role.ADMIN)){
            throw new UnauthorizedException("Access not allowed");
        }
        Optional<User> userEmailExists = userRepository.findUserByEmail(user.getEmail());
        Optional<User> userUsernameExists = userRepository.findUserByUsername(user.getUsername());
        if (userEmailExists.isPresent()){
            throw new ConflictException("Email already taken");
        }
        if (userUsernameExists.isPresent()){
            throw new ConflictException("Username already taken");
        }
        User savedUser = User.builder()
                .username(user.getUsername())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .dob(user.getDob())
                .password(passwordEncoder.encode(user.getPassword()))
                .verificationToken(utilService.getSecureRandomToken(32))
                .role(Role.USER)
                .build();
        emailService.sendVerificationMail(savedUser, savedUser.getVerificationToken());
        userRepository.save(savedUser);
    }

    @PutMapping
    public void updateUser(User requestingUser,String username,User user) throws UnauthorizedException, NotFoundException {
        if (!requestingUser.getUsername().equals(username) && !requestingUser.getRole().equals(Role.ADMIN)){
            throw new UnauthorizedException("Access not allowed");
        }
        Optional<User> userExists = userRepository.findUserByUsername(username);
        if (!userExists.isPresent()){
            throw new NotFoundException("User not found");
        }
        User existingUser = userExists.get();

        if (user.getDob()!=null){
            existingUser.setDob(user.getDob());
        }

        if (user.getFirstName()!=null){
            existingUser.setFirstName(user.getFirstName());
        }

        if (user.getLastName()!=null){
            existingUser.setLastName(user.getLastName());
        }

        userRepository.save(existingUser);
    }

    public void changePassword(User user, Map<String, String> passwordSet) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                user.getUsername(),
                passwordSet.get("oldPassword")
        ));
        User dbUser = userRepository.findUserByUsername(user.getUsername()).get();
        dbUser.setPassword(passwordEncoder.encode(passwordSet.get("newPassword")));
        userRepository.save(dbUser);
    }

    public String changeEmail(User user, Map<String, String> passwordAndEmail) throws MessagingException, ConflictException {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                user.getUsername(),
                passwordAndEmail.get("password")
        ));
        Optional<User> userEmailExists = userRepository.findUserByEmail(passwordAndEmail.get("email"));
        if (userEmailExists.isPresent()){
            throw new ConflictException("Cannot update email to given one,already taken");
        }

        User dbUser = userRepository.findUserByUsername(user.getUsername()).get();
        dbUser.setEmail(passwordAndEmail.get("email"));
        dbUser.setVerificationToken(utilService.getSecureRandomToken(32));
        emailService.sendVerificationMail(dbUser, dbUser.getVerificationToken());
        userRepository.save(dbUser);
        return "Verify new email to continue";
    }
}
