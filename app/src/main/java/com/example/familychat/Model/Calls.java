package com.example.familychat.Model;

public class Calls {
    private String userID;
    private String Time;
    private String type;
    private String direction;

    public Calls(String userID, String time, String type, String direction) {
        this.userID = userID;
        Time = time;
        this.type = type;
        this.direction = direction;
    }

    public String getUserID() {
        return userID;
    }

    public String getTime() {
        return Time;
    }

    public String getType() {
        return type;
    }

    public String getDirection() {
        return direction;
    }
}
