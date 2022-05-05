package com.example.carespace.DoctorView;

public class ReviewModel {
    private String uid,photoUrl,name,timestamp,rating, review,doctorID;

    public ReviewModel() {
    }

    public ReviewModel(String uid, String photoUrl, String name, String timestamp, String rating, String review, String doctorID) {
        this.uid = uid;
        this.photoUrl = photoUrl;
        this.name = name;
        this.timestamp = timestamp;
        this.rating = rating;
        this.review = review;
        this.doctorID = doctorID;
    }

    public String getDoctorID() {
        return doctorID;
    }

    public void setDoctorID(String doctorID) {
        this.doctorID = doctorID;
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
}
