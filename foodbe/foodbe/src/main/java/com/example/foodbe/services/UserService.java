package com.example.foodbe.services;

import com.example.foodbe.models.AppUser;
import com.example.foodbe.payload.PageResponse;
import com.example.foodbe.request.user.UserCreateDTO;
import com.example.foodbe.request.user.UserUpdateDTO;
import com.example.foodbe.response.user.UserResponseDTO;
import org.springframework.data.domain.Pageable;

public interface UserService {

    PageResponse<UserResponseDTO> findByEmail(String email, Pageable pageable);

    UserResponseDTO create(UserCreateDTO userCreateDTO);
    void deleteById(Long id);
    UserResponseDTO findById(Long id);
    AppUser findByEmail(String email);
    UserResponseDTO updateById(UserUpdateDTO userUpdateDTO);
}