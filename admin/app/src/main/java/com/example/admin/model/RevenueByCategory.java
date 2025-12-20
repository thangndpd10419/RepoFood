package com.example.admin.model;

import com.google.gson.annotations.SerializedName;

public class RevenueByCategory {
    @SerializedName("categoryId")
    private Long categoryId;

    @SerializedName("categoryName")
    private String categoryName;

    @SerializedName("revenue")
    private Double revenue;

    @SerializedName("orderCount")
    private Long orderCount;

    @SerializedName("percentage")
    private Double percentage;

    public Long getCategoryId() { return categoryId; }
    public String getCategoryName() { return categoryName != null ? categoryName : ""; }
    public Double getRevenue() { return revenue != null ? revenue : 0.0; }
    public Long getOrderCount() { return orderCount != null ? orderCount : 0L; }
    public Double getPercentage() { return percentage != null ? percentage : 0.0; }
}
