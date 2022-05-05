package com.example.carespace.Discover;

public class PostModel {
    private String profilePicUrl,name,timestamp,title,imgUrl,postID,uid;

    public PostModel() {
    }

    public PostModel(String profilePicUrl, String name, String timestamp, String title, String imgUrl, String postID, String uid) {
        this.profilePicUrl = profilePicUrl;
        this.name = name;
        this.timestamp = timestamp;
        this.title = title;
        this.imgUrl = imgUrl;
        this.postID = postID;
        this.uid = uid;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getPostID() {
        return postID;
    }

    public void setPostID(String postID) {
        this.postID = postID;
    }

    public String getProfilePicUrl() {
        return profilePicUrl;
    }

    public void setProfilePicUrl(String profilePicUrl) {
        this.profilePicUrl = profilePicUrl;
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }
}
