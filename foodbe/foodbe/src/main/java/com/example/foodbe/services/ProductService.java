package com.example.foodbe.services;

import com.example.foodbe.payload.PageResponse;
import com.example.foodbe.request.product.CreateProductDTO;
import com.example.foodbe.response.product.ProductResponseDTO;
import com.example.foodbe.request.product.UpdateProductDTO;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProductService {
//    List<ProductResponseDTO> findAll();
    PageResponse<ProductResponseDTO> findByNameContaining(String name, Pageable pageable);

    ProductResponseDTO findById(Long id);
    ProductResponseDTO create(CreateProductDTO createProductDTO);
    ProductResponseDTO updateById( UpdateProductDTO updateProductDTO);
    void deleteById(Long id);
}
