package com.example.foodbe.controllers;

import com.example.foodbe.request.auth.AuthRequest;
import com.example.foodbe.response.auth.AuthResponse;
import com.example.foodbe.payload.ApiResponse;
import com.example.foodbe.services.AuthService;
import com.example.foodbe.services.TokenService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Validated
public class AuthController {
   private final AuthService authService;
   private final TokenService tokenService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody AuthRequest authRequest) {
        AuthResponse response = authService.authenticate(authRequest);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthResponse>> refreshToken(@Valid @RequestBody String token){
        return ResponseEntity.ok(ApiResponse.success(authService.refreshToken(token)));
    }

    @PutMapping("/revoke")
    public ResponseEntity<ApiResponse<String>> revokeToken(@RequestBody String token){
        tokenService.revokeToken(token);
        return ResponseEntity.ok(ApiResponse.success("Logout"));
    }

}
