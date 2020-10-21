package com.example.opinionshare;

public class FriendsPost {
    private Posts post;
    private String mtitle;
    private String friendID;
    private boolean isvideo;

    public String getFriendID() {
        return friendID;
    }

    public void setFriendID(String friendID) {
        this.friendID = friendID;
    }

    public Posts getPost() {
        return post;
    }
    public void setPost(Posts post) {
        this.post = post;
    }

    public String getMtitle() {
        return mtitle;
    }

    public void setMtitle(String mtitle) {
        this.mtitle = mtitle;
    }

    public boolean getIsvideo() {
        return isvideo;
    }

    public void setIsvideo(boolean isvideo) {
        this.isvideo = isvideo;
    }
}
