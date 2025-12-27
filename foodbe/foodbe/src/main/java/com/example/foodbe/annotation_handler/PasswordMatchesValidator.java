package com.example.foodbe.annotation_handler;

;
import com.example.foodbe.annotation.PasswordMatches;
import com.example.foodbe.request.user.UserCreateDTO;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

//import javax.validation.ConstraintValidator;
//import javax.validation.ConstraintValidatorContext;


public class PasswordMatchesValidator implements ConstraintValidator<PasswordMatches, Object> {

    // giả sử có annotaion có tham số như @Size(min =10)
    // định nghĩa 1 biến và method initialize tác dụng trả về giá trị đó
    // khi anntiaon kgon tham số thì không cần
//    private String min;
//    @Override
//    public void initialize(PasswordMatches annotation) {
//        this.min = annotation.min();  // lấy giá trị từ @Phone(prefix = "09")
//    }

    @Override
    public boolean isValid(Object o, ConstraintValidatorContext constraintValidatorContext) {
        if (o instanceof UserCreateDTO) {
            UserCreateDTO user = (UserCreateDTO) o;
            return user.getPassword().equals(user.getConfirmPassword());
        }
        return false;
    }
}
