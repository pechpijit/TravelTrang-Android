package com.khiancode.traveltrang.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DIaryModel {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("detail")
    @Expose
    private String detail;
    @SerializedName("image")
    @Expose
    private String image;
    @SerializedName("customerId")
    @Expose
    private Integer customerId;
    @SerializedName("created_at")
    @Expose
    private Object createdAt;
    @SerializedName("updated_at")
    @Expose
    private Object updatedAt;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Integer getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }

    public Object getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Object createdAt) {
        this.createdAt = createdAt;
    }

    public Object getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Object updatedAt) {
        this.updatedAt = updatedAt;
    }

}