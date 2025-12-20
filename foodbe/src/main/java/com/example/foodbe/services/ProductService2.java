package com.example.foodbe.services;

import com.example.foodbe.payload.PageResponse;
import com.example.foodbe.request.product.CreateProductDTO;
import com.example.foodbe.request.product.UpdateProductDTO;
import com.example.foodbe.response.product.ProductResponseDTO;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ProductService2 {
    PageResponse<ProductResponseDTO> findByNameContaining(String name, Pageable pageable);

    ProductResponseDTO findById(Long id);

    ProductResponseDTO create(CreateProductDTO createProductDTO, MultipartFile file) throws IOException;

    ProductResponseDTO updateById(Long id, UpdateProductDTO updateProductDTO, MultipartFile file) throws IOException;

    void deleteById(Long id);
}
