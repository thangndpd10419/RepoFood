package com.example.foodbe.services;

import com.example.foodbe.request.auth.AuthRequest;
import com.example.foodbe.response.auth.AuthResponse;

public interface AuthService {
    AuthResponse authenticate(AuthRequest authRequest);

    AuthResponse refreshToken(String tokenRaw);
}
