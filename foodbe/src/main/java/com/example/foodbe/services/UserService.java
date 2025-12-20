package com.example.foodbe.services;

import com.example.foodbe.models.AppUser;
import com.example.foodbe.models.UserPending;
import com.example.foodbe.payload.PageResponse;
import com.example.foodbe.request.user.UserCreateDTO;
import com.example.foodbe.request.user.UserUpdateDTO;
import com.example.foodbe.response.user.UserResponseDTO;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface UserService {

    PageResponse<UserResponseDTO> findByEmail(String email, Pageable pageable);

    UserResponseDTO create(UserCreateDTO userCreateDTO);
    void deleteById(Long id);
    UserResponseDTO findById(Long id);
    AppUser findByEmail(String email);

    UserResponseDTO create2(UserPending userPending);

    UserResponseDTO update(Long id, UserUpdateDTO userUpdateDTO);
}