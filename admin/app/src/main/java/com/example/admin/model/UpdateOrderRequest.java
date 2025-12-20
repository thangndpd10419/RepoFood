package com.example.admin.model;

import com.google.gson.annotations.SerializedName;

public class UpdateOrderRequest {
    @SerializedName("status")
    private String status;

    public UpdateOrderRequest(String status) {
        this.status = status;
    }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
