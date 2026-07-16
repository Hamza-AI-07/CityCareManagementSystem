
package com.example.municipalservices.models;

public class FeedbackModel {
    private String feedbackId;
    private String complaintId;
    private String userId;
    private String userEmail;
    private String feedbackType;
    private String feedbackText;
    private long timestamp;

    public FeedbackModel() {
    }

    public FeedbackModel(String feedbackId, String complaintId, String userId, String userEmail, String feedbackType, String feedbackText, long timestamp) {
        this.feedbackId = feedbackId;
        this.complaintId = complaintId;
        this.userId = userId;
        this.userEmail = userEmail;
        this.feedbackType = feedbackType;
        this.feedbackText = feedbackText;
        this.timestamp = timestamp;
    }

    public String getFeedbackId() {
        return feedbackId;
    }

    public void setFeedbackId(String feedbackId) {
        this.feedbackId = feedbackId;
    }

    public String getComplaintId() {
        return complaintId;
    }

    public void setComplaintId(String complaintId) {
        this.complaintId = complaintId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getFeedbackType() {
        return feedbackType;
    }

    public void setFeedbackType(String feedbackType) {
        this.feedbackType = feedbackType;
    }

    public String getFeedbackText() {
        return feedbackText;
    }

    public void setFeedbackText(String feedbackText) {
        this.feedbackText = feedbackText;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
