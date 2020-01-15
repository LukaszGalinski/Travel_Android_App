package com.example.myapplication;

public class ItemDetails {
    private String info;
    private long rate;

    public ItemDetails(){
    }

    public ItemDetails(String info, long rate) {
        this.info = info;
        this.rate = rate;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public long getRate() {
        return rate;
    }

    public void setRate(long rate) {
        this.rate = rate;
    }

}
