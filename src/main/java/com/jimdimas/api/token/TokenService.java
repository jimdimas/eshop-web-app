package com.jimdimas.api.token;

import com.jimdimas.api.config.JWTService;
import com.jimdimas.api.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final TokenRepository tokenRepository;
    private final JWTService jwtService;
    private final PasswordEncoder passwordEncoder;
    public String getRefreshToken(User user){
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
}
