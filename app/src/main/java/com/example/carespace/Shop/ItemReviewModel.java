package com.example.carespace.Shop;

public class ItemReviewModel {

    private String uid,reviewID,photoUrl,name,timestamp,rating, review,itemID;

    public ItemReviewModel() { }

    public ItemReviewModel(String uid, String reviewID, String photoUrl, String name, String timestamp, String rating, String review, String itemID) {
        this.uid = uid;
        this.reviewID = reviewID;
        this.photoUrl = photoUrl;
        this.name = name;
        this.timestamp = timestamp;
        this.rating = rating;
        this.review = review;
        this.itemID = itemID;
    }

    public String getReviewID() {
        return reviewID;
    }

    public void setReviewID(String reviewID) {
        this.reviewID = reviewID;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public String getItemID() {
        return itemID;
    }

    public void setItemID(String itemID) {
        this.itemID = itemID;
    }
}
