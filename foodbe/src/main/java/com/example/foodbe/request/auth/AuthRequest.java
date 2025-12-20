package com.example.foodbe.request.auth;

import com.example.foodbe.annotation.Trim;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class AuthRequest {

        @Trim
        @NotBlank(message = "{account.username.exists}")
        @Email(message = "{account.email.invalid}")
        private String email;
        @Trim
        @NotBlank(message = "{account.password.required}")
        @Size(max = 30, min = 6, message = "{account.password.length}")
        private String password;

}
