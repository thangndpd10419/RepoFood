package com.example.foodbe.response.statistics;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Builder
@AllArgsConstructor
public class TopProductDTO {
    private Long productId;
    private String productName;
    private String imgProduct;
    private BigDecimal revenue;
    private Long quantity;
    private Long orders;
}
