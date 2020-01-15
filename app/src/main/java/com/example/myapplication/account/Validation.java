package com.example.myapplication.account;

class Validation {
    private static final String DIGIT_PATTERN= ".*\\d.*";
    private static final String HAS_UPPER_LETTER_PATTERN= ".*[A-Z].*";
    private static final int PASSWORD_LENGTH = 7;

    boolean areNotEmpty(String s1, String s2, String s3) {
        return !s1.equals("") && !s2.equals("") && !s3.equals("");
    }

    boolean passwordCheck(String password) {
        return password.length() >= PASSWORD_LENGTH && password.matches(DIGIT_PATTERN) && password.matches(HAS_UPPER_LETTER_PATTERN);
    }
}