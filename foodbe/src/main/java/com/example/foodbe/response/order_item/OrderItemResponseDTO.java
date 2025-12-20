package com.example.foodbe.response.order_item;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
public class OrderItemResponseDTO {
    private int quantity;
    private BigDecimal price;
    private String imgProduct;
    private String name;
}
