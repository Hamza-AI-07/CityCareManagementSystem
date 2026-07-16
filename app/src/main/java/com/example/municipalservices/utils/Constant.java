package com.example.municipalservices.utils;

public class Constant {

    public static final String ADMIN = "admin";
    public static final String USER = "Users";
    public static final String COMPLAINTS = "Complaints";
    public static final String FEEDBACK = "Feedback";
    public static final String ADMIN_EMAIL = "admin@gmail.com";

    public static final int MIN_TITLE_LENGTH = 5;
    public static final int MIN_DESCRIPTION_LENGTH = 10;
    public static final int MIN_PASSWORD_LENGTH = 8;
    public static final double DUPLICATE_THRESHOLD = 0.80;

    public static final String STATUS_PENDING = "pending";
    public static final String STATUS_OPEN = "open";
    public static final String STATUS_IN_PROGRESS = "in progress";
    public static final String STATUS_CLOSED = "closed";
    public static final String STATUS_REJECTED = "rejected";

    public static final String FEEDBACK_POSITIVE = "positive";
    public static final String FEEDBACK_NEGATIVE = "negative";
    public static final String FEEDBACK_PENDING = "pending";

    // Update this to match your Firebase Storage bucket exactly
    // Common formats: "project-id.appspot.com" or "project-id.firebasestorage.app"
    public static final String STORAGE_BUCKET = "citycarecms.firebasestorage.app";
}
