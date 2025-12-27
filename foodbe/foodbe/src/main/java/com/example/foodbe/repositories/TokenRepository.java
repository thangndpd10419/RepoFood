package com.example.foodbe.repositories;

import com.example.foodbe.models.AppUser;
import com.example.foodbe.models.Token;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token,Long> {
    Optional<Token> findByToken(String token);
//    List<Token> findAllByAppUserAndRevokedFalse(AppUser user);
}
