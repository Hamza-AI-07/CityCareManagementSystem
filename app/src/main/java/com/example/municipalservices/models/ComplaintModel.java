package com.example.municipalservices.models;

public class ComplaintModel {



    String complainerName, complainerPhoneNumber, complainerEmail,complainTitle,complainDescription, complainerAddress,complainType,complainStatus, complainImageURL;
    String complainerLatitude,complainerLongitude, showComplainNumberToEveryOne;
    String assignedRole, remarks, feedback;
    int likes, dislikes, complaintID;
    long timestamp;

    public ComplaintModel() {
    }

    public ComplaintModel(int complaintID,int likes, int dislikes, String complainerName, String complainerPhoneNumber, String complainerEmail, String complainTitle, String complainDescription, String complainerLatitude, String
            complainerLongitude, String complainerAddress,String complainType, String complainImageURL, String complainStatus, String showComplainNumberToEveryOne)
    {
        this(complaintID, likes, dislikes, complainerName, complainerPhoneNumber, complainerEmail, complainTitle, complainDescription, complainerLatitude, complainerLongitude, complainerAddress, complainType, complainImageURL, complainStatus, showComplainNumberToEveryOne, System.currentTimeMillis());
    }

    public ComplaintModel(int complaintID, int likes, int dislikes, String complainerName, String complainerPhoneNumber, String complainerEmail, String complainTitle, String complainDescription, String complainerLatitude, String complainerLongitude, String complainerAddress, String complainType, String complainImageURL, String complainStatus, String showComplainNumberToEveryOne, long timestamp) {
        this.complaintID = complaintID;
        this.likes = likes;
        this.dislikes = dislikes;
        this.complainerName = complainerName;
        this.complainerPhoneNumber = complainerPhoneNumber;
        this.complainerEmail = complainerEmail;
        this.complainTitle = complainTitle;
        this.complainDescription = complainDescription;
        this.complainerLatitude = complainerLatitude;
        this.complainerLongitude = complainerLongitude;
        this.complainerAddress = complainerAddress;
        this.complainType = complainType;
        this.complainImageURL = complainImageURL;
        this.complainStatus = complainStatus;
        this.showComplainNumberToEveryOne = showComplainNumberToEveryOne;
        this.timestamp = timestamp;
        this.assignedRole = "Unassigned";
        this.remarks = "No remarks yet";
        this.feedback = "none";
    }

    public String getAssignedRole() {
        return assignedRole;
    }

    public void setAssignedRole(String assignedRole) {
        this.assignedRole = assignedRole;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getComplainerName() {
        return complainerName;
    }

    public void setComplainerName(String complainerName) {
        this.complainerName = complainerName;
    }

    public String getComplainerPhoneNumber() {
        return complainerPhoneNumber;
    }

    public void setComplainerPhoneNumber(String complainerPhoneNumber) {
        this.complainerPhoneNumber = complainerPhoneNumber;
    }

    public String getComplainerEmail() {
        return complainerEmail;
    }

    public void setComplainerEmail(String complainerEmail) {
        this.complainerEmail = complainerEmail;
    }

    public String getComplainTitle() {
        return complainTitle;
    }

    public void setComplainTitle(String complainTitle) {
        this.complainTitle = complainTitle;
    }

    public String getComplainDescription() {
        return complainDescription;
    }

    public void setComplainDescription(String complainDescription) {
        this.complainDescription = complainDescription;
    }

    public String getComplainerAddress() {
        return complainerAddress;
    }

    public void setComplainerAddress(String complainerAddress) {
        this.complainerAddress = complainerAddress;
    }

    public String getComplainerLatitude() {
        return complainerLatitude;
    }

    public void setComplainerLatitude(String complainerLatitude) {
        this.complainerLatitude = complainerLatitude;
    }

    public String getComplainerLongitude() {
        return complainerLongitude;
    }

    public void setComplainerLongitude(String complainerLongitude) {
        this.complainerLongitude = complainerLongitude;
    }


    public int getLikes() {
        return likes;
    }

    public int getDislikes() {
        return dislikes;
    }

    public String getComplainType() {
        return complainType;
    }

    public void setComplainType(String complainType) {
        this.complainType = complainType;
    }

    public String getComplainImageURL() {
        return complainImageURL;
    }

    public void setComplainImageURL(String complainImageURL) {
        this.complainImageURL = complainImageURL;
    }


    public String getComplainStatus() {
        return complainStatus;
    }

    public void setComplainStatus(String complainStatus) {
        this.complainStatus = complainStatus;
    }


    public int getComplaintID() {
        return complaintID;
    }

    public void setComplaintID(int complaintID) {
        this.complaintID = complaintID;
    }


    public String getShowComplainNumberToEveryOne() {
        return showComplainNumberToEveryOne;
    }

    public void setShowComplainNumberToEveryOne(String showComplainNumberToEveryOne) {
        this.showComplainNumberToEveryOne = showComplainNumberToEveryOne;
    }
}
