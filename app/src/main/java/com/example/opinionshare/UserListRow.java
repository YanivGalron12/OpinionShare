package com.example.opinionshare;

public class UserListRow {
    String userName;
    String userID;
    String userProfilePhoto;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUserProfilePhoto() {
        return userProfilePhoto;
    }

    public void setUserProfilePhoto(String userProfilePhoto) {
        this.userProfilePhoto = userProfilePhoto;
    }

    public UserListRow(String userName, String userID, String userProfilePhoto) {
        this.userName = userName;
        this.userID = userID;
        this.userProfilePhoto = userProfilePhoto;
    }
}
