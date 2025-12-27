package com.example.foodbe.services.impls;

import com.example.foodbe.exception_handler.NotFoundException;
import com.example.foodbe.exception_handler.exception.InvalidDataException;
import com.example.foodbe.exception_handler.exception.SystemErrorException;
import com.example.foodbe.models.AppUser;
import com.example.foodbe.models.Token;
import com.example.foodbe.repositories.TokenRepository;
import com.example.foodbe.response.token.TokenResponseDTO;
import com.example.foodbe.services.TokenService;
import com.example.foodbe.utils.ConstantUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class TokenServiceImpl implements TokenService {

    private final TokenRepository tokenRepository;


    private static final int TOKEN_BYTE_LENGTH = 32;
    private static final long TOKEN_EXPIRE_HOURS = 24 * 7; // 7 ngày

    private final SecureRandom secureRandom = new SecureRandom();

    // Tạo refresh token mới
    @Override
    public String createToken(AppUser user) {
        byte[] bytes = new byte[TOKEN_BYTE_LENGTH];
        secureRandom.nextBytes(bytes);

        // token raw (gửi cho client)
        String tokenRaw = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);

        // hash token lưu DB
        String tokenHash = hashToken(tokenRaw);

        Token token = Token.builder()
                .token(tokenHash)
                .revoked(false)
                .user(user)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusHours(TOKEN_EXPIRE_HOURS))
                .build();

        tokenRepository.save(token);

        return tokenRaw; // tra về token chưa hash
    }

    // Validate token
    @Override
    public Token validateToken(String tokenRaw) {
        String tokenHash= hashToken(tokenRaw);
        Token token = tokenRepository.findByToken(tokenRaw)
                .orElseThrow(()-> new NotFoundException(ConstantUtils.ExceptionMessage.NOT_FOUND+"xxx"));

        if (token.isRevoked()) throw new InvalidDataException(ConstantUtils.ExceptionMessage.TOKEN_IS_REVOKED);
        if (token.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new InvalidDataException(ConstantUtils.ExceptionMessage.TOKEN_IS_EXPIRED);
        }

        return token;
    }

    // Revoke token
    @Override
    public void revokeToken(String token) {
        Token t = tokenRepository.findByToken(token)
                        .orElseThrow(()-> new NotFoundException(ConstantUtils.ExceptionMessage.NOT_FOUND));

        t.setRevoked(true);
        tokenRepository.save(t);
    }

    private String hashToken(String tokenRaw) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(tokenRaw.getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new SystemErrorException("Error hashing refresh token", e);
        }
    }

}
