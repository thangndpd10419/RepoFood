package com.example.foodbe.controllers;

import com.example.foodbe.payload.ApiResponse;
import com.example.foodbe.response.statistics.*;
import com.example.foodbe.services.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/statistics")
@RequiredArgsConstructor

public class StatisticsController {

    private final StatisticsService statisticsService;

    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<DashboardStatsDTO>> getDashboardStats() {
        DashboardStatsDTO stats = statisticsService.getDashboardStats();
        return ResponseEntity.ok(ApiResponse.success(stats, "Lấy thống kê dashboard thành công"));
    }

    @GetMapping("/revenue/daily")
    public ResponseEntity<ApiResponse<List<RevenueByDateDTO>>> getRevenueByDay(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        List<RevenueByDateDTO> data = statisticsService.getRevenueByDay(startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success(data, "Lấy doanh thu theo ngày thành công"));
    }

    @GetMapping("/revenue/monthly")
    public ResponseEntity<ApiResponse<List<RevenueByDateDTO>>> getRevenueByMonth(
            @RequestParam(defaultValue = "#{T(java.time.LocalDate).now().getYear()}") int year) {

        List<RevenueByDateDTO> data = statisticsService.getRevenueByMonth(year);
        return ResponseEntity.ok(ApiResponse.success(data, "Lấy doanh thu theo tháng thành công"));
    }

    @GetMapping("/revenue/yearly")
    public ResponseEntity<ApiResponse<List<RevenueByDateDTO>>> getRevenueByYear() {
        List<RevenueByDateDTO> data = statisticsService.getRevenueByYear();
        return ResponseEntity.ok(ApiResponse.success(data, "Lấy doanh thu theo năm thành công"));
    }

    @GetMapping("/revenue/category")
    public ResponseEntity<ApiResponse<List<RevenueByCategoryDTO>>> getRevenueByCategory() {
        List<RevenueByCategoryDTO> data = statisticsService.getRevenueByCategory();
        return ResponseEntity.ok(ApiResponse.success(data, "Lấy doanh thu theo danh mục thành công"));
    }

    @GetMapping("/top-products")
    public ResponseEntity<ApiResponse<List<TopProductDTO>>> getTopProducts(
            @RequestParam(defaultValue = "10") int limit) {

        List<TopProductDTO> data = statisticsService.getTopProducts(limit);
        return ResponseEntity.ok(ApiResponse.success(data, "Lấy top sản phẩm bán chạy thành công"));
    }

    @GetMapping("/top-products/range")
    public ResponseEntity<ApiResponse<List<TopProductDTO>>> getTopProductsInRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "10") int limit) {

        List<TopProductDTO> data = statisticsService.getTopProductsInRange(startDate, endDate, limit);
        return ResponseEntity.ok(ApiResponse.success(data, "Lấy top sản phẩm trong khoảng thời gian thành công"));
    }
}
