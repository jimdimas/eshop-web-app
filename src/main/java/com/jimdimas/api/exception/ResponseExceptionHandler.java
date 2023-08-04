package com.jimdimas.api.exception;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.fasterxml.jackson.databind.util.JSONPObject;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class ResponseExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value=IllegalStateException.class)
    protected ResponseEntity<Object> illegalStateHandler(
            RuntimeException exception, WebRequest request
            ){
        Map<String,String> responseBody = new HashMap<String,String>();
        responseBody.put("message", exception.getMessage());
        return handleExceptionInternal(
                exception,
                responseBody,
                new HttpHeaders(),
                HttpStatus.BAD_REQUEST,request);
    }

    @ExceptionHandler(value=Exception.class)
    protected ResponseEntity<Object> jwtVerificationHandler(
            RuntimeException exception,
            WebRequest request){
        Map<String,String> responseBody = new HashMap<String,String>();
        responseBody.put("message", "Something went wrong,try again");
        return handleExceptionInternal(
                exception,
                responseBody,
                new HttpHeaders(),
                HttpStatus.INTERNAL_SERVER_ERROR,
                request
        );
    }
}
