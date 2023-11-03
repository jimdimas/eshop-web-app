package com.jimdimas.api.config;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.jimdimas.api.user.User;
import com.jimdimas.api.util.UtilService;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.util.Date;


@Service
public class JWTService {
    private RSAPrivateKey privateKey;
    private RSAPublicKey publicKey;
    private final UtilService utilService;

    public JWTService(@Value("${spring.properties.jwt.private_key}") String stringPrivateKey,
                      @Value("${spring.properties.jwt.public_key}") String stringPublicKey){
        utilService=new UtilService();
        try {
            publicKey=utilService.convertToRSAPublicKey(stringPublicKey);
            privateKey=utilService.convertToRSAPrivateKey(stringPrivateKey);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }
    }

    public String extractSubject(String token) throws JWTVerificationException, TokenExpiredException {
        try {
            DecodedJWT decodedJWT = this.decodeToken(token);
            return decodedJWT.getSubject();
        }
        catch (Exception e){
            return null;
        }
    }

    public String generateAccessToken(UserDetails userDetails) throws JWTCreationException {
        Integer oneHour = 1000 * 60*60 ;
        return generateToken(userDetails,oneHour);
    }

    public String generateRefreshToken(UserDetails user) {
        Integer oneDay = 1000*60*60*24;
        return generateToken(user,oneDay);
    }

    public Boolean verifyToken(String token) throws JWTVerificationException,TokenExpiredException {
        try {
            DecodedJWT decodedJWT = this.decodeToken(token);    //token validity is checked in decodeToken,if its not valid exception is thrown
            return decodedJWT.getExpiresAt().after(new Date(System.currentTimeMillis()));
        } catch (Exception e){
            return false;
        }
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

    private String generateToken(UserDetails userDetails,Integer duration){
        Algorithm algorithm = Algorithm.RSA256(this.publicKey, this.privateKey);
        return JWT.create()
                .withIssuer("auth0")
                .withSubject(userDetails.getUsername())
                .withIssuedAt(new Date(System.currentTimeMillis()))
                .withExpiresAt(new Date(System.currentTimeMillis() + duration))
                .sign(algorithm);
    }
}
