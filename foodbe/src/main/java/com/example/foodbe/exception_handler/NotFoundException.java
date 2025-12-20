package com.example.foodbe.exception_handler;

public class NotFoundException extends RuntimeException{
    public NotFoundException(String message){
        super(message);

    }
    public  NotFoundException(String message, Throwable cause){
        super(message,cause);
    }
}
