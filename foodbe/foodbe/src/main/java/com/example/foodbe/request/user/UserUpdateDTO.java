package com.example.foodbe.request.user;

import com.example.foodbe.annotation.FormatWhitespace;
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
public class UserUpdateDTO {

    @NotNull
    private Long id;

    @FormatWhitespace
    @Size(max = 30, message = "{account.fullName.length}")
    private String name;

    @Trim
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
    @Pattern(regexp = "^0[0-9]{9}$", message = "{account.phone.pattern}")
    private String phone;

    private UserStatus status;

    private Role role;
}
