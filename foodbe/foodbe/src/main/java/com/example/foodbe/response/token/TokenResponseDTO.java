package com.example.foodbe.response.token;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class TokenResponseDTO {

    private String refreshToken; // token dạng string( chỉ random chưa sha)
}


