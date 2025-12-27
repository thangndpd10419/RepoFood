package com.example.foodbe.services;

import com.example.foodbe.models.AppUser;
import com.example.foodbe.models.Token;
import com.example.foodbe.response.token.TokenResponseDTO;

import java.util.Optional;

public interface TokenService {
    // Tạo refresh token mới cho user
    String createToken(AppUser user);

    // Validate refresh token: check tồn tại, revoked, expired
    // return token để biết đag dùng user nào mà tạo jwt
    Token validateToken(String tokenHash);

    // Revoke token
    void revokeToken(String token);


}
