package com.jimdimas.api.exception;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class ResponseExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = BadCredentialsException.class)
    protected ResponseEntity<Object> badCredentialsHandler(
            RuntimeException exception,WebRequest request
    ) {
        return baseExceptionHandler(exception, request, "Invalid credentials provided", HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(value=IllegalStateException.class)
    protected ResponseEntity<Object> illegalStateHandler(
            RuntimeException exception, WebRequest request
            ){
        return baseExceptionHandler(exception, request, exception.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value=Exception.class)
    protected ResponseEntity<Object> generalHandler(
            RuntimeException exception,
            WebRequest request){
        return baseExceptionHandler(exception,request,"Something went wrong,please try again.",HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<Object> baseExceptionHandler(
            RuntimeException exception,
            WebRequest request,
            String message,
            HttpStatus status){
        Map<String,String> responseBody = new HashMap<String,String>();
        responseBody.put("message", message);
        return handleExceptionInternal(
                exception,
                responseBody,
                new HttpHeaders(),
                status,
                request
        );
    }
}
