package com.example.familychat.Model;

public class Chats {
    private String time;
    private String message;
    private String type;
    private String sender;
    private String receiver;



    public Chats(String time, String message, String type, String sender, String receiver) {
        this.time = time;
        this.message = message;
        this.type = type;
        this.sender = sender;
        this.receiver = receiver;
    }

    public String getDateTime() {
        return time;
    }
    public String getTextMessage() {
        return message;
    }
    public String getType() {
        return type;
    }

    public String getSender() {
        return sender;
    }

    public String getReceiver() {
        return receiver;
    }

}

