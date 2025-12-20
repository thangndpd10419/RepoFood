package com.example.admin.model;

import com.google.gson.annotations.SerializedName;

public class TopProduct {
    @SerializedName("productId")
    private Long productId;

    @SerializedName("productName")
    private String productName;

    @SerializedName("productImage")
    private String productImage;

    @SerializedName("revenue")
    private Double revenue;

    @SerializedName("quantitySold")
    private Long quantitySold;

    @SerializedName("orderCount")
    private Long orderCount;

    public Long getProductId() { return productId; }
    public String getProductName() { return productName != null ? productName : ""; }
    public String getProductImage() { return productImage; }
    public Double getRevenue() { return revenue != null ? revenue : 0.0; }
    public Long getQuantitySold() { return quantitySold != null ? quantitySold : 0L; }
    public Long getOrderCount() { return orderCount != null ? orderCount : 0L; }
}
