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

    @FormatWhitespace
    @Size(max = 30, message = "{account.fullName.length}")
    private String name;

    @Trim
    @Email(message = "{account.email.invalid}")
    private String email;

    @Trim
    @Pattern(regexp = "^0[0-9]{9}$", message = "{account.phone.pattern}")
    private String phone;

    private UserStatus status;

    private Role role;
}
