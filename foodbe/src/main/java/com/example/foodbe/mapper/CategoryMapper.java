package com.example.foodbe.mapper;

import com.example.foodbe.models.AppUser;
import com.example.foodbe.response.category.CategoryResponseDTO;
import com.example.foodbe.request.category.CreateCategoryDTO;
import com.example.foodbe.request.category.UpdateCategoryDTO;
import com.example.foodbe.models.Category;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class CategoryMapper {

    public Category toEntity(CreateCategoryDTO requestDTO, AppUser user){
        return Category.builder()
                .name(requestDTO.getName())
                .imgCategory(requestDTO.getImgCategory())
                .user(user)
                .build();
    }

    public CategoryResponseDTO toDTO(Category category){
        return CategoryResponseDTO.builder()
                .id(category.getId())
                .name(category.getName())
                .imgCategory(category.getImgCategory())
                .createAt(category.getCreatedAt())
                .productCount(0)
                .build();
    }

    public CategoryResponseDTO toDTO(Category category, Integer productCount){
        return CategoryResponseDTO.builder()
                .id(category.getId())
                .name(category.getName())
                .imgCategory(category.getImgCategory())
                .createAt(category.getCreatedAt())
                .productCount(productCount)
                .build();
    }

    public void updateEntityFromDto(UpdateCategoryDTO dto, Category entity) {
        if (dto == null || entity == null) return;
        entity.setName(dto.getName());
        entity.setImgCategory(dto.getImgCategory());

    }

}
