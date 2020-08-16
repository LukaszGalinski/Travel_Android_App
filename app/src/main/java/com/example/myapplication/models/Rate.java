package com.example.myapplication.models;

public class Rate {
    private String comment;
    private float rate;
    private String date;
    private boolean accept;
    private String person;

    public Rate(String comment, float rate, String date, boolean accept, String person) {
        this.comment = comment;
        this.rate = rate;
        this.date = date;
        this.accept = accept;
        this.person = person;
    }

    public Rate() {
    }

    public Rate(String comment, float rate) {
        this.comment = comment;
        this.rate = rate;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public float getRate() {
        return rate;
    }

    public void setRate(float rate) {
        this.rate = rate;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public boolean isAccept() {
        return accept;
    }

    public void setAccept(boolean accept) {
        this.accept = accept;
    }

    public String getPerson() {
        return person;
    }

    public void setPerson(String person) {
        this.person = person;
    }
}
