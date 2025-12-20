package com.example.admin.model;

import com.google.gson.annotations.SerializedName;

public class RevenueStats {
    @SerializedName("date")
    private String date;

    @SerializedName("month")
    private Integer month;

    @SerializedName("year")
    private Integer year;

    @SerializedName("totalRevenue")
    private Double totalRevenue;

    @SerializedName("totalOrders")
    private Integer totalOrders;

    @SerializedName("label")
    private String label;

    public RevenueStats() {}

    public RevenueStats(String label, Double totalRevenue, Integer totalOrders) {
        this.label = label;
        this.totalRevenue = totalRevenue;
        this.totalOrders = totalOrders;
    }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public Integer getMonth() { return month; }
    public void setMonth(Integer month) { this.month = month; }

    public Integer getYear() { return year; }
    public void setYear(Integer year) { this.year = year; }

    public Double getTotalRevenue() { return totalRevenue; }
    public void setTotalRevenue(Double totalRevenue) { this.totalRevenue = totalRevenue; }

    public Integer getTotalOrders() { return totalOrders; }
    public void setTotalOrders(Integer totalOrders) { this.totalOrders = totalOrders; }

    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }
}
