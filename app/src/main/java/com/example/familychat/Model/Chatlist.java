package com.example.familychat.Model;

public class Chatlist {

    private String userID;
    private String userName;
    private String date;
    private String urlProfile;

    public Chatlist(String userID, String userName, String date, String urlProfile) {
        this.userID = userID;
        this.userName = userName;
        this.date = date;
        this.urlProfile = urlProfile;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }


    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getUrlProfile() {
        return urlProfile;
    }

    public void setUrlProfile(String urlProfile) {
        this.urlProfile = urlProfile;
    }
}
