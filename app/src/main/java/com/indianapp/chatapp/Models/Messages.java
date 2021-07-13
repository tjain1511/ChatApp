package com.indianapp.chatapp.Models;

public class Messages {
    private String messageId;
    private String message;
    private String receiverId;
    private String senderId;
    private String senderName;
    private String timeStamp;
    private String messageStatus;

    public Messages(String messageId, String message, String receiverId, String senderId, String senderName, String timeStamp, String messageStatus) {
        this.messageId = messageId;
        this.message = message;
        this.receiverId = receiverId;
        this.senderId = senderId;
        this.senderName = senderName;
        this.timeStamp = timeStamp;
        this.messageStatus = messageStatus;
    }

    public String getMessageId() {
        return messageId;
    }

    public String getMessage() {
        return message;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public String getSenderId() {
        return senderId;
    }

    public String getSenderName() {
        return senderName;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public String getMessageStatus() {
        return messageStatus;
    }

    public void setMessageStatus(String messageStatus) {
        this.messageStatus = messageStatus;
    }
}
