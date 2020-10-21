package com.example.opinionshare;

public class PostForSale {
    private Posts post;
    private String ownerId;
    private String timeStamp;
    private String company;
    private String type;
    private double price;

    public Posts getPost() {
        return post;
    }

    public void setPost(Posts post) {
        this.post = post;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp=timeStamp;
    }

}

