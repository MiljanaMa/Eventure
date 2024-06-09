package com.example.eventure.model;

import java.util.Date;

public class Rating {
    private String id;
    private String companyId;
    private int rating;
    private String comment;
    private java.util.Date createdOn;
    private String userId;
    private User user;
    private boolean reported;

    public Rating(String id, String companyId, int rating, String comment, java.util.Date createdOn, String userId) {
        this.id = id;
        this.companyId = companyId;
        this.rating = rating;
        this.comment = comment;
        this.createdOn = createdOn;
        this.userId = userId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public boolean isReported() {
        return reported;
    }

    public void setReported(boolean reported) {
        this.reported = reported;
    }

    public Rating(){}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public java.util.Date getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Date createdOn) {
        this.createdOn = createdOn;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
