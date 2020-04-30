package com.example.opinionshare;

import java.util.Date;
import java.util.List;

public class Posts {

    private String name;
    private String email;
    private String userId;
    private String userType;
    private String phoneNumber;
    private String profilePhotoUri;
    private Integer age, likeCounter;
    private Date creationDate;
    private List<String> categoryList;
    private String postPhotoUri;
    private String caption;
    private String tags;


    public Posts(String postPhotoUri, String caption, String tags, String name, String email, String userId, String userType, String phoneNumber, String profilePhotoUri, Integer age, Integer likeCounter, Date creationDate, List<String> categoryList) {
        this.name = name;
        this.email = email;
        this.userId = userId;
        this.userType = userType;
        this.phoneNumber = phoneNumber;
        this.profilePhotoUri = profilePhotoUri;
        this.age = age;
        this.likeCounter = likeCounter;
        this.creationDate = creationDate;
        this.categoryList = categoryList;
        this.tags= tags;
        this.caption = caption;
        this.postPhotoUri = postPhotoUri;
    }

    public String getPostPhotoUri() {
        return postPhotoUri;
    }

    public void setPostPhotoUri(String postPhotoUri) {
        this.postPhotoUri = postPhotoUri;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getProfilePhotoUri() {
        return profilePhotoUri;
    }

    public void setProfilePhotoUri(String profilePhotoUri) {
        this.profilePhotoUri = profilePhotoUri;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Integer getLikeCounter() {
        return likeCounter;
    }

    public void setLikeCounter(Integer likeCounter) {
        this.likeCounter = likeCounter;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public List<String> getCategoryList() {
        return categoryList;
    }

    public void setCategoryList(List<String> categoryList) {
        this.categoryList = categoryList;
    }
}
