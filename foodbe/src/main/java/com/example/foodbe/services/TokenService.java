package com.example.foodbe.services;

import com.example.foodbe.models.AppUser;
import com.example.foodbe.models.Token;
import com.example.foodbe.response.token.TokenResponseDTO;

import java.util.Optional;

public interface TokenService {

    String createToken(AppUser user);

    Token validateToken(String tokenHash);

    void revokeToken(String token);

}
