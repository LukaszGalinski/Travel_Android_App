package com.example.myapplication.views.account;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

class Validation {
    private static final String DIGIT_PATTERN= ".*\\d.*";
    private static final String HAS_UPPER_LETTER_PATTERN= ".*[A-Z].*";
    private static final int PASSWORD_LENGTH = 7;
    private static Pattern digitPatternCompiled = Pattern.compile(DIGIT_PATTERN);
    private static Pattern upperLetterPatternCompiled = Pattern.compile(HAS_UPPER_LETTER_PATTERN);

    static boolean areNotEmpty(String s1, String s2, String s3) {
        return !s1.equals("") && !s2.equals("") && !s3.equals("");
    }

    static boolean passwordCheck(String password) {
        Matcher digitMatcher = digitPatternCompiled.matcher(password);
        Matcher upperLetterMatcher = upperLetterPatternCompiled.matcher(password);
        return password.length() >= PASSWORD_LENGTH && digitMatcher.matches() && upperLetterMatcher.matches();
    }
}