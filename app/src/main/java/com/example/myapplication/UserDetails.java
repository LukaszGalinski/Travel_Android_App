package com.example.myapplication;

public class UserDetails {
    private String name;
    private String surname;
    private String age;
    private String aboutMe;

    public UserDetails(String name, String surname, String age, String aboutMe) {
        this.name = name;
        this.surname = surname;
        this.age = age;
        this.aboutMe = aboutMe;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getAboutMe() {
        return aboutMe;
    }

    public void setAboutMe(String aboutMe) {
        this.aboutMe = aboutMe;
    }
}
