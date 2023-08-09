package com.jimdimas.api.util;

import java.security.SecureRandom;
import java.util.Base64;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

@Service
public class UtilService {

    public RSAPublicKey convertToRSAPublicKey(String publicKey) throws NoSuchAlgorithmException, InvalidKeySpecException {
        if (!publicKey.contains("-----BEGIN PUBLIC KEY-----") || !publicKey.contains("-----END PUBLIC KEY-----"))
        {
            throw new InvalidKeySpecException("Wrong RSA key format");
        }
        publicKey = publicKey
                .replace("-----BEGIN PUBLIC KEY-----","")
                .replace("\n","")
                .replace("-----END PUBLIC KEY-----","")
                .replace(" ","");
        byte[] publicKeyToBytes = Base64.getDecoder().decode(publicKey);
        return (RSAPublicKey) KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(publicKeyToBytes));
    }

    public RSAPrivateKey convertToRSAPrivateKey(String privateKey) throws NoSuchAlgorithmException, InvalidKeySpecException {
        if (!privateKey.contains("-----BEGIN PRIVATE KEY-----") || !privateKey.contains("-----END PRIVATE KEY-----"))
        {
            throw new InvalidKeySpecException("Wrong RSA key format");
        }
        privateKey = privateKey
                .replace("-----BEGIN PRIVATE KEY-----","")
                .replace("\n","")
                .replace("-----END PRIVATE KEY-----","")
                .replace(" ","");
        byte[] privateKeyToBytes = java.util.Base64.getDecoder().decode(privateKey);
        return (RSAPrivateKey) KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(privateKeyToBytes));
    }

    public String objectToJson(Object object) throws JsonProcessingException {
        if (object == null) {
            return null;
        }
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(object);
    }

    public String getSecureRandomToken(Integer length){
        String allChars = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        SecureRandom secureRandom = new SecureRandom();
        StringBuilder tokenBuilder = new StringBuilder();
        for (int i=0; i<length; i++){
            tokenBuilder.append(allChars.charAt(secureRandom.nextInt(allChars.length())));
        }
        return tokenBuilder.toString();
    }

}
