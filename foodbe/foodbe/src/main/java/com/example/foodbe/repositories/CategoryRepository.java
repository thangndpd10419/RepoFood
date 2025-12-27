package com.example.foodbe.repositories;

import com.example.foodbe.models.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


public interface CategoryRepository extends JpaRepository<Category,Long> {
    boolean existsByName(String name);

    Page<Category> findByNameContaining(String name, Pageable pageable);
    // Containing : = %like%

}
