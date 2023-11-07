package com.jimdimas.api.token;

import com.jimdimas.api.config.JWTService;
import com.jimdimas.api.exception.UnauthorizedException;
import com.jimdimas.api.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final TokenRepository tokenRepository;
    private final JWTService jwtService;
    private final PasswordEncoder passwordEncoder;
    public String getRefreshToken(User user){
        Optional<Token> hasToken =tokenRepository.findByUsername(user.getUsername());
        if (hasToken.isPresent()){
            tokenRepository.delete(hasToken.get());
        }
        Integer oneDay = 1000*60*60*24;
        String refreshToken = jwtService.generateRefreshToken(user);
        Token token = Token.builder()
                .refreshToken(passwordEncoder.encode(refreshToken)) //the token hash is saved in database only
                .user(user)
                .expiresAt(new Date(System.currentTimeMillis()+oneDay))
                .build();
        tokenRepository.save(token);
        return refreshToken;
    }

    public User verifyByRefreshToken(String refreshToken) throws UnauthorizedException {
        if (!jwtService.verifyToken(refreshToken)){
            throw new UnauthorizedException("Unauthorized to access this route");
        }

        String username= jwtService.extractSubject(refreshToken);
        Optional<Token> tokenExists = tokenRepository.findByUsername(username);

        if (!tokenExists.isPresent()){
            throw new UnauthorizedException("Unauthorized to access this route");
        }

        Token token = tokenExists.get();

        if (!BCrypt.checkpw(refreshToken,token.getRefreshToken()))
        {
            tokenRepository.delete(token);
            throw new UnauthorizedException("Unauthorized to access this route");
        }

        return token.getUser();
    }
}
