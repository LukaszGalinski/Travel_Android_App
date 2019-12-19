package com.example.myapplication.account;

public class Validation {

    public boolean areNotEmpty(String s1, String s2, String s3){
        if (s1.equals("") || s2.equals("") || s3.equals("")) return false;
        else return true;
    }

    public boolean passwordCheck(String password){
        if (password.length()<7 || !password.matches(".*\\d.*") || !password.matches(".*[A-Z].*")) return false;
        else return true;
}

}
