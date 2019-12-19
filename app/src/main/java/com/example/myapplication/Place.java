package com.example.myapplication;

import android.widget.ArrayAdapter;

public class Place {
    private String address;
    private String name;
    private String phone;
    private String category;
    private String placeid;
    private String info;
    private String infoen;
    private String website;

    public Place(){}

    public Place(String address, String name, String phone, String category, String placeid, String info, String infoen, String website) {
        this.address = address;
        this.name = name;
        this.phone = phone;
        this.category = category;
        this.placeid = placeid;
        this.info = info;
        this.infoen = infoen;
        this.website = website;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getPlaceid() {
        return placeid;
    }
    public void setPlaceid(String placeid) {
        this.placeid = placeid;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getInfoen() {
        return infoen;
    }

    public void setInfoen(String infoen) {
        this.infoen = infoen;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }
}
