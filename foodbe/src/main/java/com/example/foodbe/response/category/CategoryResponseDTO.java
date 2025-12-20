package com.example.foodbe.response.category;

import lombok.*;

import java.time.LocalDateTime;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
public class CategoryResponseDTO {
    private Long id;
    private String name;
    private String imgCategory;
    private LocalDateTime createAt;
    private Integer productCount;
}
