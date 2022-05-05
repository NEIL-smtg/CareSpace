package com.example.carespace.Exercise;

public class dbTutorialModel {
    private String tutorialID,description,imgUrl,levelInfo,title;

    public dbTutorialModel() {
    }

    public dbTutorialModel(String tutorialID, String description, String imgUrl, String levelInfo, String title) {
        this.tutorialID = tutorialID;
        this.description = description;
        this.imgUrl = imgUrl;
        this.levelInfo = levelInfo;
        this.title = title;
    }

    public String getTutorialID() {
        return tutorialID;
    }

    public void setTutorialID(String tutorialID) {
        this.tutorialID = tutorialID;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getLevelInfo() {
        return levelInfo;
    }

    public void setLevelInfo(String levelInfo) {
        this.levelInfo = levelInfo;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
