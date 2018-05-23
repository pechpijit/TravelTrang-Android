package com.khiancode.traveltrang.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GroupCommentModel {

    @SerializedName("customer")
    @Expose
    private UserModel customer;

    @SerializedName("comment")
    @Expose
    private CommentModel comment;

    public UserModel getCustomer() {
        return customer;
    }

    public void setCustomer(UserModel customer) {
        this.customer = customer;
    }

    public CommentModel getComment() {
        return comment;
    }

    public void setComment(CommentModel comment) {
        this.comment = comment;
    }
}