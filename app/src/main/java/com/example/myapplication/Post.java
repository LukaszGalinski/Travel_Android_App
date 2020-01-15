package com.example.myapplication;

public class Post {
    private String userName;
    private String userEmail;
    private String message;
    private String date;

    public Post(){

    }
    public Post(String date,  String userEmail, String userName, String message){
        this.userName = userName;
        this.userEmail = userEmail;
        this.message = message;
        this.date = date;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
