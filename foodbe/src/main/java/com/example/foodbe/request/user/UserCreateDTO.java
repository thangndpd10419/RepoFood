package com.example.foodbe.request.user;

import com.example.foodbe.annotation.FormatWhitespace;
import com.example.foodbe.annotation.PasswordMatches;
import com.example.foodbe.annotation.Trim;
import com.example.foodbe.models.Role;
import com.example.foodbe.models.UserStatus;
import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@PasswordMatches
public class UserCreateDTO {

    @FormatWhitespace
    @NotBlank(message = "{account.fullName.required}")
    @Size(max = 30, message = "{account.fullName.length}")
    private String name;

    @Trim
    @NotBlank(message = "{account.email.required}")
    @Email(message = "{account.email.invalid}")
    private String email;

    @Trim
    @NotBlank(message = "{account.password.required}")
    @Size(min = 6, max = 30, message = "{account.password.length}")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\W).{6,}$",
            message = "{account.password.pattern}")
    private String password;

    @Trim
    private String confirmPassword;

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
