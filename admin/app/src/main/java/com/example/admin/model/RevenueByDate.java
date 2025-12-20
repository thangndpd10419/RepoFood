package com.example.admin.model;

import com.google.gson.annotations.SerializedName;

public class RevenueByDate {
    @SerializedName("date")
    private String date;

    @SerializedName("revenue")
    private Double revenue;

    @SerializedName("orders")
    private Long orders;

    public String getDate() { return date != null ? date : ""; }
    public Double getRevenue() { return revenue != null ? revenue : 0.0; }
    public Long getOrders() { return orders != null ? orders : 0L; }
}
