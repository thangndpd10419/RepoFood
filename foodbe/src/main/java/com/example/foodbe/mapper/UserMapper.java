package com.example.foodbe.mapper;

import com.example.foodbe.request.user.UserCreateDTO;
import com.example.foodbe.response.user.UserResponseDTO;
import com.example.foodbe.models.AppUser;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public AppUser toEntity(UserCreateDTO userCreateDTO){
        return AppUser.builder()
                .name(userCreateDTO.getName())
                .email(userCreateDTO.getEmail())
                .password(userCreateDTO.getPassword())
                .phone(userCreateDTO.getPhone())
                .status(userCreateDTO.getStatus())
                .role(userCreateDTO.getRole())
                .build();
    }

    public UserResponseDTO toDto(AppUser user){
        return UserResponseDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .status(user.getStatus())
                .role(user.getRole())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
