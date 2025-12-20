package com.example.foodbe.annotation_handler;

import com.example.foodbe.annotation.PasswordMatches;
import com.example.foodbe.request.user.UserCreateDTO;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordMatchesValidator implements ConstraintValidator<PasswordMatches, Object> {

    @Override
    public boolean isValid(Object o, ConstraintValidatorContext constraintValidatorContext) {
        if (o instanceof UserCreateDTO) {
            UserCreateDTO user = (UserCreateDTO) o;
            return user.getPassword().equals(user.getConfirmPassword());
        }
        return false;
    }
}
