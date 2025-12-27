package com.example.foodbe.repositories;

import com.example.foodbe.models.Product;
import com.example.foodbe.response.product.ProductResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ProductRepository extends JpaRepository<Product,Long> {

    Page<Product> findByNameContaining(String name, Pageable pageable);

}
