package com.example.foodbe.request.auth;


import com.example.foodbe.annotation.Trim;
import com.example.foodbe.utils.ConstantUtils;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

//import javax.validation.constraints.Email;
//import javax.validation.constraints.NotBlank;
//import javax.validation.constraints.Size;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class AuthRequest {

        @Trim
        @NotBlank(message = "{account.username.blank}")
        @Email(message = "{account.email.invalid}")
        private String email;
        @Trim
        @NotBlank(message = "{account.password.required}")
        @Size(max = ConstantUtils.ValidateMessgae.PASSMAX
                , min = ConstantUtils.ValidateMessgae.PASSMIN
                , message = "{account.password.length}")
        private String password;


}
