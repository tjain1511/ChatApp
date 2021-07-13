package com.indianapp.chatapp.Models;

public class UserModel {
    private String UId;
    private String email;
    private String imageUrl;
    private String username;
    private String fcmToken;
    private String lastMessage;
    private int numberOfUnreadMsg;
    private String time;

    public UserModel(String UId, String email, String imageUrl, String username, String fcmToken) {
        this.UId = UId;
        this.email = email;
        this.imageUrl = imageUrl;
        this.username = username;
        this.fcmToken = fcmToken;

    }

    public String getLastMessage() {
        return lastMessage;
    }

    public UserModel(String UId, String imageUrl, String username, String lastMessage, int numberOfUnreadMsg, String time) {
        this.UId = UId;
        this.imageUrl = imageUrl;
        this.username = username;
        this.lastMessage = lastMessage;
        this.numberOfUnreadMsg = numberOfUnreadMsg;
        this.time = time;
    }

    public String getTime() {
        return time;
    }

    public int getNumberOfUnreadMsg() {
        return numberOfUnreadMsg;
    }

    public String getFcmToken() {
        return fcmToken;
    }

    public String getUId() {
        return UId;
    }

    public String getEmail() {
        return email;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getUsername() {
        return username;
    }
}
