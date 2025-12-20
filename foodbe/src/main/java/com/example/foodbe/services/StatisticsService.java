package com.example.foodbe.services;

import com.example.foodbe.response.statistics.*;

import java.time.LocalDate;
import java.util.List;

public interface StatisticsService {

    DashboardStatsDTO getDashboardStats();

    List<RevenueByDateDTO> getRevenueByDay(LocalDate startDate, LocalDate endDate);

    List<RevenueByDateDTO> getRevenueByMonth(int year);

    List<RevenueByDateDTO> getRevenueByYear();

    List<RevenueByCategoryDTO> getRevenueByCategory();

    List<TopProductDTO> getTopProducts(int limit);

    List<TopProductDTO> getTopProductsInRange(LocalDate startDate, LocalDate endDate, int limit);
}
