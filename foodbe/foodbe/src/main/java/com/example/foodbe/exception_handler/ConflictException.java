package com.example.foodbe.exception_handler;

public class ConflictException extends RuntimeException{
    public ConflictException(String message){
        super(message);
    }
    public ConflictException(String message, Throwable cause){
        super(message,cause);
    }
}
