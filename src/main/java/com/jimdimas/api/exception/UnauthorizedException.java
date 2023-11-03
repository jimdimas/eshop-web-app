package com.jimdimas.api.exception;

import org.springframework.http.HttpStatus;

public class UnauthorizedException extends CustomException{

    public UnauthorizedException(String message){
        super(message);
        this.statusCode=HttpStatus.UNAUTHORIZED;
    }
}
