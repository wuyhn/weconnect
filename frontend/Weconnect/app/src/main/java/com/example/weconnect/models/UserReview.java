package com.example.weconnect.models;

import java.io.Serializable;

public class UserReview implements Serializable {
    private String reviewerName;
    private float rating;
    private String comment;

    public UserReview(String reviewerName, float rating, String comment) {
        this.reviewerName = reviewerName;
        this.rating = rating;
        this.comment = comment;
    }

    public String getReviewerName() {
        return reviewerName;
    }

    public float getRating() {
        return rating;
    }

    public String getComment() {
        return comment;
    }
}