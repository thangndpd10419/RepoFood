package com.example.foodbe.request.order;

import com.example.foodbe.models.OrderStatus;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateOrderDTO {
    private BigDecimal totalPrice;
    private OrderStatus status;
    private int table;
    private String note;
    private Long userId;
}
