package com.example.opinionshare;

import java.util.ArrayList;
import java.util.List;


public class Member {
    private String name;
    private String email;
    private String userId;
    private String userType;
    private String username;
    private String phoneNumber;
    private String profilePhotoUri;
    private String postimageuri;

    private Integer age;
    private ArrayList<String> friendList;
    private ArrayList<String> categoryList;
    private ArrayList<String> devicesToken;
    private ArrayList<Posts> PostList;


    public String getPostimageuri() {
        return postimageuri;
    }

    public void setPostimageuri(String postimageuri) {
        this.postimageuri = postimageuri;
    }


    public Member() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public Member(String userId, String name, String email, String profilePhotoUri, String phoneNumber, String username,ArrayList<String> devicesToken) {
        this.name = name;
        this.email = email;
        this.userId = userId;
        this.userType = "Basic";
        this.username = username;
        this.phoneNumber = phoneNumber;
        this.profilePhotoUri = profilePhotoUri;
        this.age = 1000;
        this.devicesToken = devicesToken;
        this.friendList = new ArrayList<String>();// TODO: add friendList to constructor
        this.categoryList = new ArrayList<String>();
        this.PostList = new ArrayList<Posts>();

    }

    public ArrayList<String> getDevicesToken() {
        return devicesToken;
    }

    public void setDevicesToken(ArrayList<String> devicesToken) {
        this.devicesToken = devicesToken;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public String getProfilePhotoUri() {
        return profilePhotoUri;
    }

    public void setProfilePhotoUri(String profilePhotoUri) {
        this.profilePhotoUri = profilePhotoUri;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public List<String> getFriendList() {
        return friendList;
    }

    public void setFriendList(ArrayList<String> friendList) {
        this.friendList = friendList;
    }

    public List<String> getCategoryList() {
        return categoryList;
    }

    public void setCategoryList(List<String> categoryList) {
        this.categoryList = (ArrayList<String>) categoryList;
    }

    public List<Posts> getPostList() {
        return PostList;
    }

    public Posts getPostByPosition(int position) {
        return PostList.get(position);
    }

    public void setPostList(List<Posts> PostList) {
        this.PostList = (ArrayList<Posts>) PostList;
    }

}
