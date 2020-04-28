package com.example.opinionshare;

public class UserToShow {
    private String username;
    private String id;

    public UserToShow() {
    }

    public UserToShow(String username, String id) {
        this.username = username;
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}
