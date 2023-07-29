package com.jimdimas.api.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JWTService {

    @Value("${spring.properties.jwt.secret_key}")   //get key from application.properties file
    private String secretKey;

    private Claims extractAllClaims(String token){
        return Jwts.
                parserBuilder().
                setSigningKey(getSigningKey()).
                build().
                parseClaimsJwt(token).
                getBody();
    }

    private <T> T extractClaim(String token, Function<Claims,T> claimsResolver){
        Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }
    public String generateToken(Map<String,Object> claims, UserDetails userDetails){
        Integer twoHoursMillis=1000*60*2;
        return Jwts
                .builder()
                .setSubject(userDetails.getUsername())
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis()+twoHoursMillis))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();

    }

    public String generateToken(UserDetails userDetails){   //used when we don't provide any extra claims apart from user email
        return generateToken(new HashMap<String,Object>(),userDetails);
    }

    private boolean isValidToken(String token,UserDetails userDetails){ //checks if token has not expired and if it's issued to the sender
        return (extractClaim(token,Claims::getSubject).equals(userDetails.getUsername()) &&
                extractClaim(token,Claims::getExpiration).before(new Date()));
    }

    private Key getSigningKey() {
        byte[] keyInBytes = Decoders.BASE64.decode(this.secretKey);
        return Keys.hmacShaKeyFor(keyInBytes);
    }
}
