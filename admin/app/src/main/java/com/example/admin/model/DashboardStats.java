package com.example.admin.model;

import com.google.gson.annotations.SerializedName;

public class DashboardStats {
    @SerializedName("todayRevenue")
    private Double todayRevenue;

    @SerializedName("yesterdayRevenue")
    private Double yesterdayRevenue;

    @SerializedName("monthRevenue")
    private Double monthRevenue;

    @SerializedName("totalOrders")
    private Long totalOrders;

    @SerializedName("todayOrders")
    private Long todayOrders;

    @SerializedName("totalCustomers")
    private Long totalCustomers;

    @SerializedName("totalProducts")
    private Long totalProducts;

    @SerializedName("totalCategories")
    private Long totalCategories;

    @SerializedName("revenueGrowth")
    private Double revenueGrowth;

    @SerializedName("orderGrowth")
    private Double orderGrowth;

    public Double getTodayRevenue() { return todayRevenue != null ? todayRevenue : 0.0; }
    public Double getYesterdayRevenue() { return yesterdayRevenue != null ? yesterdayRevenue : 0.0; }
    public Double getMonthRevenue() { return monthRevenue != null ? monthRevenue : 0.0; }
    public Long getTotalOrders() { return totalOrders != null ? totalOrders : 0L; }
    public Long getTodayOrders() { return todayOrders != null ? todayOrders : 0L; }
    public Long getTotalCustomers() { return totalCustomers != null ? totalCustomers : 0L; }
    public Long getTotalProducts() { return totalProducts != null ? totalProducts : 0L; }
    public Long getTotalCategories() { return totalCategories != null ? totalCategories : 0L; }
    public Double getRevenueGrowth() { return revenueGrowth; }
    public Double getOrderGrowth() { return orderGrowth; }
}
