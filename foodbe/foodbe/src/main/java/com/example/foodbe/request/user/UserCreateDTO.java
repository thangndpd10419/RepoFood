package com.example.foodbe.request.user;

import com.example.foodbe.annotation.FormatWhitespace;
import com.example.foodbe.annotation.PasswordMatches;
import com.example.foodbe.annotation.Trim;
import com.example.foodbe.models.Role;
import com.example.foodbe.models.UserStatus;
import com.example.foodbe.utils.ConstantUtils;
import jakarta.validation.constraints.*;
import lombok.*;

//import javax.validation.constraints.*;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@PasswordMatches
public class UserCreateDTO {

    @FormatWhitespace
    @NotBlank(message = "{account.fullName.required}")
    @Size(max = ConstantUtils.ValidateMessgae.NAME_MAX, message = "{account.fullName.length}")
    private String name;

//    @NotNull(message = "{account.age.required}")
//    @Min(value = 18, message = "{account.age.length}")
//    private Integer age;

    @Trim
    @NotBlank(message = "{account.email.required}")
    @Email(message = "{account.email.invalid}")
    private String email;

    @Trim
    @NotBlank(message = "{account.password.required}")
    @Size(min = ConstantUtils.ValidateMessgae.PASSMIN, max = ConstantUtils.ValidateMessgae.PASSMAX, message = "{account.password.length}")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\W).{6,}$",
            message = "{account.password.pattern}")
    private String password;

    @Trim
    private String confirmPassword;

//    private String address;

    @Trim
    @Pattern
            (regexp = "^0[0-9]{9}$",
             message = "{account.phone.pattern}")
    private String phone;

    @NotNull(message = "Status staff")
    private UserStatus status;

    @NotNull(message = "{role.empty}")
    private Role role;
}
