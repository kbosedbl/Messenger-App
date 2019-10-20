package com.example.messenger__;

public class User {
    public String username,userId,imageURL,email;/*status;*/
    public User() {
        this.username = "";
        this.userId = "";
        this.imageURL = "";
        this.email="";
        //this.status="";
    }
    public User(String username, String userId, String imageURL,String email,String status) {
        this.username = username;
        this.userId = userId;
        this.imageURL = imageURL;
        this.email=email;
        //this.status=status;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    /*public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }*/
}
