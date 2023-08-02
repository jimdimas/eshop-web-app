package com.jimdimas.api.config;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;
import org.hibernate.cfg.Environment;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JWTService {
    @Value("${spring.properties.jwt.private_key}")
    private String actualPrivateKey;
    @Value("${spring.properties.jwt.public_key}")
    private String actualPublicKey;
    //The following keys are RSA keys objects , we read the actual values and then convert them to keys
    private RSAPrivateKey privateKey;
    private RSAPublicKey publicKey;

    public String extractSubject(String token) throws JWTVerificationException{
        DecodedJWT decodedJWT = this.decodeToken(token);
        return decodedJWT.getSubject();
    }

    public String generateToken(UserDetails userDetails) throws JWTCreationException {
        Integer twoHours = 1000 * 60 * 60 * 2;
        Algorithm algorithm = Algorithm.RSA256(this.publicKey, this.privateKey);
        return JWT.create()
                .withIssuer("auth0")
                .withSubject(userDetails.getUsername())
                .withIssuedAt(new Date(System.currentTimeMillis()))
                .withExpiresAt(new Date(System.currentTimeMillis() + twoHours))
                .sign(algorithm);

    }

    public Boolean verifyToken(String token) throws JWTVerificationException {
        DecodedJWT decodedJWT = this.decodeToken(token);    //token validity is checked in decodeToken,if its not valid exception is thrown
        return decodedJWT.getExpiresAt().before(new Date(System.currentTimeMillis()));
    }

    private DecodedJWT decodeToken(String token) throws JWTVerificationException{
        Algorithm algorithm = Algorithm.RSA256(this.publicKey, this.privateKey);
        DecodedJWT decodedJWT;
        JWTVerifier jwtVerifier = JWT
                .require(algorithm)
                .withIssuer("auth0")
                .build();
        return jwtVerifier.verify(token);
    }
}
