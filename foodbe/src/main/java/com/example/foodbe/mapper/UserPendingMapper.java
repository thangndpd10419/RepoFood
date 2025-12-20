package com.example.foodbe.mapper;

import com.example.foodbe.models.AppUser;
import com.example.foodbe.models.UserPending;
import com.example.foodbe.request.user.UserCreateDTO;
import org.springframework.stereotype.Component;

@Component
public class UserPendingMapper {
    public UserCreateDTO toUserDto(UserPending userPending){
        return UserCreateDTO.builder()
                .name(userPending.getName())
                .email(userPending.getEmail())
                .password(userPending.getPassword())
                .confirmPassword(userPending.getConfirmPassword())
                .phone(userPending.getPhone())
                .status(userPending.getStatus())
                .role(userPending.getRole())
                .build();
    }
    public UserPending toEntity(UserCreateDTO userCreateDTO){
        return UserPending.builder()
                .name(userCreateDTO.getName())
                .email(userCreateDTO.getEmail())
                .password(userCreateDTO.getPassword())
                .confirmPassword(userCreateDTO.getConfirmPassword())
                .phone(userCreateDTO.getPhone())
                .status(userCreateDTO.getStatus())
                .role(userCreateDTO.getRole())
                .build();
    }

    public AppUser toAppUser(UserPending userPending){
        return AppUser.builder()
                .name(userPending.getName())
                .email(userPending.getEmail())
                .password(userPending.getPassword())
                .phone(userPending.getPhone())
                .status(userPending.getStatus())
                .role(userPending.getRole())
                .build();
    }
}
