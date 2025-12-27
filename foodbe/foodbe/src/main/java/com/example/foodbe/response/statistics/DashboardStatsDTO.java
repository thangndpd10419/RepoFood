package com.example.foodbe.response.statistics;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DashboardStatsDTO {
    private BigDecimal todayRevenue;
    private BigDecimal yesterdayRevenue;
    private BigDecimal monthRevenue;
    private Long totalOrders;
    private Long todayOrders;
    private Long totalCustomers;
    private Long totalProducts;
    private Long totalCategories;
    private Double revenueGrowth;
    private Double orderGrowth;
}
