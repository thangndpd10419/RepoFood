package com.example.foodbe.services;

import com.example.foodbe.payload.PageResponse;
import com.example.foodbe.response.category.CategoryResponseDTO;
import com.example.foodbe.request.category.CreateCategoryDTO;
import com.example.foodbe.request.category.UpdateCategoryDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;


public interface CategoryService {
//    List<CategoryResponseDTO> findAll(); // khong cần find all vì đa co findContainng
    // khi name = "" => like ="%%" = find All

    PageResponse<CategoryResponseDTO> findByNameContaining( String name, Pageable pageable);


    CategoryResponseDTO findById(Long id);
    CategoryResponseDTO create(CreateCategoryDTO createCategoryDTO);
    CategoryResponseDTO updateById(UpdateCategoryDTO updateCategoryDTO);
    void deleteById(Long id);
}
