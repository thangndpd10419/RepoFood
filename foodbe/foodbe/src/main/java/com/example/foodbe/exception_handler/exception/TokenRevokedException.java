package com.example.foodbe.exception_handler.exception;

public class TokenRevokedException extends RuntimeException {
    public TokenRevokedException(String message) {
        super(message);
    }

    // bị thu hồi : 403 forbidden
}
