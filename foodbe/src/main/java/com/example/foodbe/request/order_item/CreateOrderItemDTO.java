package com.example.foodbe.request.order_item;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateOrderItemDTO {
    private int quantity;
    private BigDecimal price;
    private Long productId;
    private Long OrderId;
}
