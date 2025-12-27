package com.example.foodbe.response.statistics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
@Builder
public class RevenueByYearDTO {
    private Integer year;
    private BigDecimal revenue;
    private Long orders;
}
