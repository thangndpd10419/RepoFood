package com.example.foodbe.exception_handler.exception;

public class TokenNotFoundException extends RuntimeException {
    public TokenNotFoundException(String message) {
        super(message);
    }

    // token không đúng 404
}
