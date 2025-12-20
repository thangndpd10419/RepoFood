package com.example.staff.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class CreateOrderRequest {
    @SerializedName("userId")
    private Long userId;

    @SerializedName("table")
    private Integer table;

    @SerializedName("note")
    private String note;

    @SerializedName("orderDetails")
    private List<OrderDetailRequest> orderDetails;

    public CreateOrderRequest(Long userId, Integer table, String note, List<OrderDetailRequest> orderDetails) {
        this.userId = userId;
        this.table = table;
        this.note = note;
        this.orderDetails = orderDetails;
    }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Integer getTable() { return table; }
    public void setTable(Integer table) { this.table = table; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    public List<OrderDetailRequest> getOrderDetails() { return orderDetails; }
    public void setOrderDetails(List<OrderDetailRequest> orderDetails) { this.orderDetails = orderDetails; }

    public static class OrderDetailRequest {
        @SerializedName("productId")
        private Long productId;

        @SerializedName("quantity")
        private Integer quantity;

        public OrderDetailRequest(Long productId, Integer quantity) {
            this.productId = productId;
            this.quantity = quantity;
        }

        public Long getProductId() { return productId; }
        public void setProductId(Long productId) { this.productId = productId; }

        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }
    }
}
