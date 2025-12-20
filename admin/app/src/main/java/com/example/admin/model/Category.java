package com.example.admin.model;

import com.google.gson.annotations.SerializedName;

public class Category {
    @SerializedName("id")
    private Long id;

    @SerializedName("name")
    private String name;

    @SerializedName("imgCategory")
    private String imgCategory;

    @SerializedName("userId")
    private Long userId;

    @SerializedName("createAt")
    private String createdAt;

    @SerializedName("updatedAt")
    private String updatedAt;

    @SerializedName("productCount")
    private Integer productCount;

    public Category() {}

    public Category(String name, String imgCategory, Long userId) {
        this.name = name;
        this.imgCategory = imgCategory;
        this.userId = userId;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getImgCategory() { return imgCategory; }
    public void setImgCategory(String imgCategory) { this.imgCategory = imgCategory; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }

    public Integer getProductCount() { return productCount; }
    public void setProductCount(Integer productCount) { this.productCount = productCount; }
}
