package com.example.foodbe.request.order;

import com.example.foodbe.models.OrderStatus;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateOrderDTO {
    private BigDecimal totalPrice;
    private OrderStatus status;
    private int table;
    private String note;
    private Long userId;
    private List<OrderDetailDTO> orderDetails;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class OrderDetailDTO {
        private Long productId;
        private Integer quantity;
    }
}
