package com.example.foodbe.exception_handler.exception;

public class TokenExpiredException extends RuntimeException {
    public TokenExpiredException(String message) {
        super(message);
    }

    // hết hạn 401
}
