package com.example.carespace.Chat;

public class ChatModel {

    private String sender, receiver, message, senderName, receiverName,senderImg,receiverImg,timestamp;

    public ChatModel(String sender, String receiver, String message, String senderName, String receiverName, String senderImg, String receiverImg, String timestamp) {
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
        this.senderName = senderName;
        this.receiverName = receiverName;
        this.senderImg = senderImg;
        this.receiverImg = receiverImg;
        this.timestamp = timestamp;
    }

    public ChatModel() { }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public String getSenderImg() {
        return senderImg;
    }

    public void setSenderImg(String senderImg) {
        this.senderImg = senderImg;
    }

    public String getReceiverImg() {
        return receiverImg;
    }

    public void setReceiverImg(String receiverImg) {
        this.receiverImg = receiverImg;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
