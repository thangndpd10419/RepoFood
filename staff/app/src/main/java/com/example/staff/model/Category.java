package com.example.staff.model;

import com.google.gson.annotations.SerializedName;

public class Category {
    @SerializedName("id")
    private Long id;

    @SerializedName("name")
    private String name;

    @SerializedName("imgCategory")
    private String imgCategory;

    @SerializedName("description")
    private String description;

    public Category() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getImgCategory() { return imgCategory; }
    public void setImgCategory(String imgCategory) { this.imgCategory = imgCategory; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
