package com.example.carespace.Chat;

public class ChatUserListModel {

    String receiverName,timestamp,receiverImg,lastMessage,receiverID;

    public ChatUserListModel() { }

    public ChatUserListModel(String receiverName, String timestamp, String receiverImg, String lastMessage, String receiverID) {
        this.receiverName = receiverName;
        this.timestamp = timestamp;
        this.receiverImg = receiverImg;
        this.lastMessage = lastMessage;
        this.receiverID = receiverID;
    }

    public String getReceiverID() {
        return receiverID;
    }

    public void setReceiverID(String receiverID) {
        this.receiverID = receiverID;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getReceiverImg() {
        return receiverImg;
    }

    public void setReceiverImg(String receiverImg) {
        this.receiverImg = receiverImg;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }
}
