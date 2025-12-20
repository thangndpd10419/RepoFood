package com.example.foodbe.services;

import com.example.foodbe.models.UserPending;
import com.example.foodbe.request.user.UserCreateDTO;

import java.util.List;

public interface UserPendingService {
    void create(UserCreateDTO userCreateDTO);
    List<UserPending> findAll();
    void deleteById(Long id);
}
