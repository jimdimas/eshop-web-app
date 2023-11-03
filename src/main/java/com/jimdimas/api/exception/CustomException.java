package com.jimdimas.api.exception;

import lombok.Data;
import org.springframework.http.HttpStatusCode;

@Data
public abstract class CustomException extends Exception{

    HttpStatusCode statusCode;

    public CustomException(String message) {
        super(message);
    }

    public CustomException() {
    }
}
