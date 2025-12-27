package com.example.foodbe.annotation;

import com.example.foodbe.annotation_handler.PasswordMatchesValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

//import javax.validation.Constraint;
import java.lang.annotation.*;

@Target({ElementType.TYPE}) // Đúng chuẩn: TYPE cho DTO/class
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PasswordMatchesValidator.class)
@Documented
public @interface PasswordMatches {

    String message() default "Passwords do not match";
    Class<?>[] groups() default {}; // bắt buộc khi dùng @Constraint
    Class<? extends Payload>[] payload() default {}; // bắt buộc tương tự.
    // khi dùng custom anotaion vơi Constraint, bắt buộc phải có message, group, payload.
}
