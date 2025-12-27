package com.example.foodbe.response.statistics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
@AllArgsConstructor
public class RevenueByMonthDTO {
    private Integer year;
    private Integer month;
    private BigDecimal revenue;
    private Long orders;
}
