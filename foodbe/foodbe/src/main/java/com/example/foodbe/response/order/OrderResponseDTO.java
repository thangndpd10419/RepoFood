package com.example.foodbe.response.order;

import com.example.foodbe.models.OrderStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
public class OrderResponseDTO {
    private Long id;
    private BigDecimal totalPrice;
    private OrderStatus status;
    private int table;
    private String note;
}
