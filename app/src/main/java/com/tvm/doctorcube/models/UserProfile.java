package com.tvm.doctorcube.models;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.PropertyName;

public class UserProfile {
    @PropertyName("name")
    private String name;

    @PropertyName("email")
    private String email;

    @PropertyName("mobile")
    private String mobile;

    @PropertyName("country")
    private String country;

    @PropertyName("state")
    private String state;

    @PropertyName("city")
    private String city;

    @PropertyName("neetScore")
    private String neetScore;

    @PropertyName("studyCountry")
    private String studyCountry;

    @PropertyName("universityName")
    private String universityName;

    @PropertyName("lastCallDate")
    private String lastCallDate;

    @PropertyName("hasNeetScore")
    private boolean hasNeetScore;

    @PropertyName("hasPassport")
    private boolean hasPassport;

    @PropertyName("isAdmitted")
    private boolean isAdmitted;

    @PropertyName("isApplied")
    private boolean isApplied;

    @PropertyName("lastUpdatedDate")
    private String lastUpdatedDate;

    @PropertyName("timestamp")
    private Timestamp timestamp;

    // Default constructor for Firestore
    public UserProfile() {}

    // Getters and setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getMobile() { return mobile; }
    public void setMobile(String mobile) { this.mobile = mobile; }
    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }
    public String getState() { return state; }
    public void setState(String state) { this.state = state; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public String getNeetScore() { return neetScore; }
    public void setNeetScore(String neetScore) { this.neetScore = neetScore; }
    public String getStudyCountry() { return studyCountry; }
    public void setStudyCountry(String studyCountry) { this.studyCountry = studyCountry; }
    public String getUniversityName() { return universityName; }
    public void setUniversityName(String universityName) { this.universityName = universityName; }
    public String getLastCallDate() { return lastCallDate; }
    public void setLastCallDate(String lastCallDate) { this.lastCallDate = lastCallDate; }
    public boolean isHasNeetScore() { return hasNeetScore; }
    public void setHasNeetScore(boolean hasNeetScore) { this.hasNeetScore = hasNeetScore; }
    public boolean isHasPassport() { return hasPassport; }
    public void setHasPassport(boolean hasPassport) { this.hasPassport = hasPassport; }
    public boolean isAdmitted() { return isAdmitted; }
    public void setAdmitted(boolean admitted) { isAdmitted = admitted; }
    public boolean isApplied() { return isApplied; }
    public void setApplied(boolean applied) { isApplied = applied; }
    public String getLastUpdatedDate() { return lastUpdatedDate; }
    public void setLastUpdatedDate(String lastUpdatedDate) { this.lastUpdatedDate = lastUpdatedDate; }
    public Timestamp getTimestamp() { return timestamp; }
    public void setTimestamp(Timestamp timestamp) { this.timestamp = timestamp; }
}