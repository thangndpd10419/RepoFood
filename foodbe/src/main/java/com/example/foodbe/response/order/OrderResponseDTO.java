package com.example.foodbe.response.order;

import com.example.foodbe.models.OrderStatus;
import com.example.foodbe.response.order_item.OrderItemResponseDTO;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
public class OrderResponseDTO {
    private Long id;
    private BigDecimal totalPrice;
    private OrderStatus status;
    private int table;
    private String note;
    private Long userId;
    private String userName;
    private LocalDateTime createdAt;
    private List<OrderItemResponseDTO> orderDetails;
}
