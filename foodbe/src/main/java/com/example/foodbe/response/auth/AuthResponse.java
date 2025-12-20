package com.example.foodbe.response.auth;

import lombok.*;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class AuthResponse {
    private Long userId;
    private String email;
    private String role;
    private String accessToken;
    private String refreshToken;

}
