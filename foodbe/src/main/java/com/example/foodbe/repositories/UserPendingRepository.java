package com.example.foodbe.repositories;

import com.example.foodbe.models.UserPending;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserPendingRepository extends JpaRepository<UserPending, Long> {
    boolean existsByEmail(String email);
}
