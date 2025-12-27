package com.example.foodbe.response.user;

import com.example.foodbe.models.Role;
import com.example.foodbe.models.UserStatus;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class UserResponseDTO {
    private Long id;
    private String name;
//    private int age;
    private String email;
//    private String address;
    private String phone;
    private UserStatus status;
    private Role role;
}
