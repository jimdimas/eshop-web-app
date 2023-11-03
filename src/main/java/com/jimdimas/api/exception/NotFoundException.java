package com.jimdimas.api.exception;

import org.springframework.http.HttpStatus;

public class NotFoundException extends CustomException{

    public NotFoundException(String message){
        super(message);
        this.statusCode = HttpStatus.NOT_FOUND;
    }
}
