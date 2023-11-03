package com.jimdimas.api.exception;

import org.springframework.http.HttpStatus;

public class BadRequestException extends CustomException{
    public BadRequestException(String message){
        super(message);
        this.statusCode=HttpStatus.BAD_REQUEST;
    }
}
