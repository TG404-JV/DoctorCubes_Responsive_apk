package com.tvm.doctorcube.adminpannel.databsemanager;


public class Student {
    private String id;
    private String name;
    private String mobile;
    private String email;
    private String state;
    private String city;
    private String interestedCountry;
    private String hasNeetScore;
    private String neetScore;
    private String hasPassport;
    private String callStatus;
    private boolean isInterested;
    private boolean admitted;
    private String submissionDate;
    private String firebasePushDate;
    private String lastCallDate;
    private String collection;

    // Default constructor for Gson
    public Student() {}

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getMobile() { return mobile; }
    public void setMobile(String mobile) { this.mobile = mobile; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getState() { return state; }
    public void setState(String state) { this.state = state; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public String getInterestedCountry() { return interestedCountry; }
    public void setInterestedCountry(String interestedCountry) { this.interestedCountry = interestedCountry; }
    public String getHasNeetScore() { return hasNeetScore; }
    public void setHasNeetScore(String hasNeetScore) { this.hasNeetScore = hasNeetScore; }
    public String getNeetScore() { return neetScore; }
    public void setNeetScore(String neetScore) { this.neetScore = neetScore; }
    public String getHasPassport() { return hasPassport; }
    public void setHasPassport(String hasPassport) { this.hasPassport = hasPassport; }
    public String getCallStatus() { return callStatus; }
    public void setCallStatus(String callStatus) { this.callStatus = callStatus; }
    public boolean isInterested() { return isInterested; }
    public void setIsInterested(boolean isInterested) { this.isInterested = isInterested; }
    public boolean isAdmitted() { return admitted; }
    public void setAdmitted(boolean admitted) { this.admitted = admitted; }
    public String getSubmissionDate() { return submissionDate; }
    public void setSubmissionDate(String submissionDate) { this.submissionDate = submissionDate; }
    public String getFirebasePushDate() { return firebasePushDate; }
    public void setFirebasePushDate(String firebasePushDate) { this.firebasePushDate = firebasePushDate; }
    public String getLastCallDate() { return lastCallDate; }
    public void setLastCallDate(String lastCallDate) { this.lastCallDate = lastCallDate; }
    public String getCollection() { return collection; }
    public void setCollection(String collection) { this.collection = collection; }
}
