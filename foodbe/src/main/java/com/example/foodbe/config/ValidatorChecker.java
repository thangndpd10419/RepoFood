package com.example.foodbe.config;

import org.springframework.validation.Validator;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ValidatorChecker {

    @Autowired
    private Validator validator;

    @PostConstruct
    public void checkValidator() {
        System.out.println("Validator class: " + validator.getClass());
    }
}
