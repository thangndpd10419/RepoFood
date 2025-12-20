package com.example.foodbe.response.user;

import com.example.foodbe.models.Role;
import com.example.foodbe.models.UserStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class UserResponseDTO {
    private Long id;
    private String name;
    private String email;
    private String phone;
    private UserStatus status;
    private Role role;
    private LocalDateTime createdAt;
}
