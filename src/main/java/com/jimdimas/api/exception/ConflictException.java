package com.jimdimas.api.exception;

import org.springframework.http.HttpStatus;

public class ConflictException extends CustomException{

    public ConflictException(String message){
        super(message);
        this.statusCode=HttpStatus.CONFLICT;
    }
}
