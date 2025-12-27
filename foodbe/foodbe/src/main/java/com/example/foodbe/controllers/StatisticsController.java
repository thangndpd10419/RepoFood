package com.example.foodbe.controllers;


import com.example.foodbe.payload.ApiResponse;
import com.example.foodbe.response.statistics.*;
import com.example.foodbe.services.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/statistics")
@Validated
public class StatisticsController {
    private final StatisticsService statisticsService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<DashboardStatsDTO>> getDashboardStats() {
        DashboardStatsDTO stats = statisticsService.getDashboardStats();
        return ResponseEntity.ok(ApiResponse.success(stats, "Lấy thống kê dashboard thành công"));
    }

    //GET /revenue/daily?startDate=2025-12-01&endDate=2025-12-25
    //@DateTimeFormat(iso = DateTimeFormat.ISO.DATE): convert chuỗi từ query parameter thành LocalDate / LocalDateTime / Date.
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/revenue/daily")
    public ResponseEntity<ApiResponse<List<RevenueByDateDTO>>> getRevenueByDay(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        List<RevenueByDateDTO> data = statisticsService.getRevenueByDay(startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success(data, "Lấy doanh thu theo ngày thành công"));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/revenue/monthly")
    public ResponseEntity<ApiResponse<List<RevenueByMonthDTO>>> getRevenueByMonth(
            @RequestParam(defaultValue = "#{T(java.time.LocalDate).now().getYear()}") int year) {

        List<RevenueByMonthDTO> data = statisticsService.getRevenueByMonth(year);
        return ResponseEntity.ok(ApiResponse.success(data, "Lấy doanh thu theo tháng thành công"));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/revenue/yearly")
    public ResponseEntity<ApiResponse<List<RevenueByYearDTO>>> getRevenueByYear() {
        List<RevenueByYearDTO> data = statisticsService.getRevenueByYear();
        return ResponseEntity.ok(ApiResponse.success(data, "Lấy doanh thu theo năm thành công"));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/revenue/category")
    public ResponseEntity<ApiResponse<List<RevenueByCategoryDTO>>> getRevenueByCategory() {
        List<RevenueByCategoryDTO> data = statisticsService.getRevenueByCategory();
        return ResponseEntity.ok(ApiResponse.success(data, "Lấy doanh thu theo danh mục thành công"));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/top-products")
    public ResponseEntity<ApiResponse<List<TopProductDTO>>> getTopProducts(
            @RequestParam(defaultValue = "10") int limit) {

        List<TopProductDTO> data = statisticsService.getTopProducts(limit);
        return ResponseEntity.ok(ApiResponse.success(data, "Lấy top sản phẩm bán chạy thành công"));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/top-products/range")
    public ResponseEntity<ApiResponse<List<TopProductDTO>>> getTopProductsInRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "10") int limit) {

        List<TopProductDTO> data = statisticsService.getTopProductsInRange(startDate, endDate, limit);
        return ResponseEntity.ok(ApiResponse.success(data, "Lấy top sản phẩm trong khoảng thời gian thành công"));
    }
}
