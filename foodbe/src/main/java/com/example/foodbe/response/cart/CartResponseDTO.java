package com.example.foodbe.response.cart;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
public class CartResponseDTO {
    private Long id;
    private String name;
    private int quantity;
    private BigDecimal price;
    private String imgProduct;
}
