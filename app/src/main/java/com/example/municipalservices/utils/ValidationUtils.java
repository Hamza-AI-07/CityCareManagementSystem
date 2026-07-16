package com.example.municipalservices.utils;

import android.text.TextUtils;
import android.util.Patterns;

public class ValidationUtils {

    public static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static boolean isValidName(String name) {
        return !TextUtils.isEmpty(name) && name.trim().length() >= Constant.MIN_TITLE_LENGTH;
    }

    public static boolean isValidTitle(String title) {
        return !TextUtils.isEmpty(title) && title.trim().length() >= Constant.MIN_TITLE_LENGTH;
    }

    public static boolean isValidDescription(String description) {
        return !TextUtils.isEmpty(description) && description.trim().length() >= Constant.MIN_DESCRIPTION_LENGTH;
    }

    public static boolean isValidPassword(String password) {
        return !TextUtils.isEmpty(password) && password.length() >= Constant.MIN_PASSWORD_LENGTH;
    }

    public static boolean isValidPhoneNumber(String phone) {
        return !TextUtils.isEmpty(phone) && phone.length() >= 10;
    }

    public static boolean doPasswordsMatch(String password, String confirmPassword) {
        return !TextUtils.isEmpty(password) && password.equals(confirmPassword);
    }
}
