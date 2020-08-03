package com.example.opinionshare;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Posts {

    private Integer likeCounter;
    private String creationDate;
    private String category;
    private String postUri;
    private String postType;
    private String caption;
    private String description;

    public Posts() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public Posts(String description, String postUri, String postType, String caption,  Integer likeCounter, String creationDate, String category) {
        this.description = description;
        this.likeCounter = likeCounter;
        this.creationDate = creationDate;
        this.category = category;
        this.caption = caption;
        this.postUri = postUri;
        this.postType = postType;
    }

    public String getPostUri() {
        return postUri;
    }

    public void setPostUri(String postUri) {
        this.postUri = postUri;
    }

    public String getPostType() {
        return postType;
    }

    public void setPostType(String postType) {
        this.postType = postType;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getLikeCounter() {
        return likeCounter;
    }

    public void setLikeCounter(Integer likeCounter) {
        this.likeCounter = likeCounter;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

}
