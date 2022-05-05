package com.example.carespace.Exercise;

public class CommentModel {

    private String uid,photoUrl,timestamp,name,comment;

    public CommentModel(){}

    public CommentModel(String uid, String photoUrl, String timestamp, String name, String comment) {
        this.uid = uid;
        this.photoUrl = photoUrl;
        this.timestamp = timestamp;
        this.name = name;
        this.comment = comment;
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

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
