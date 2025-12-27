package com.example.foodbe.response.product;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class ProductResponseDTO {
    private Long id;
    private String name;
    private int quantity;
    private BigDecimal price;
    private String imgProduct;
    private String description;
    private Long idCategory;
}
