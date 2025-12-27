package com.example.foodbe.response.statistics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RevenueByCategoryDTO {
    private Long categoryId;
    private String categoryName;
    private BigDecimal revenue;
    private Long orderCount;
}
