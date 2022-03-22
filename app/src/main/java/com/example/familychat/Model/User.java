package com.example.familychat.Model;

import android.text.TextUtils;

public class User {
    private String userID;
    private String userName;
    private String userImageURL;

    public User(String userID, String userName, String userImageURL) {
        this.userID = userID;
        this.userName = userName;
        this.userImageURL = userImageURL;
    }

    public String getUserID() {
        return userID;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserImageURL() {
        return userImageURL;
    }
}
