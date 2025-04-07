package com.tvm.doctorcube.adminpannel;

import android.os.Parcel;
import android.os.Parcelable;

public class Student implements Parcelable {
    private String id, name, mobile, email, state, city, interestedCountry, hasNeetScore, neetScore, hasPassport,
            submissionDate, callStatus, lastCallDate, lastUpdatedDate, firebasePushDate;
    private boolean isInterested, isAdmitted;
    private String collection; // Added field to store the collection name

    // Default constructor required for Firebase
    public Student() {}

    // Constructor for Parcelable
    protected Student(Parcel in) {
        id = in.readString();
        name = in.readString();
        mobile = in.readString();
        email = in.readString();
        state = in.readString();
        city = in.readString();
        interestedCountry = in.readString();
        hasNeetScore = in.readString();
        neetScore = in.readString();
        hasPassport = in.readString();
        submissionDate = in.readString();
        callStatus = in.readString();
        lastCallDate = in.readString();
        lastUpdatedDate = in.readString();
        firebasePushDate = in.readString();
        isInterested = in.readByte() != 0; // Boolean from byte
        isAdmitted = in.readByte() != 0;   // Boolean from byte
        collection = in.readString(); // Added: Read collection from Parcel
    }

    // Creator required for Parcelable
    public static final Creator<Student> CREATOR = new Creator<Student>() {
        @Override
        public Student createFromParcel(Parcel in) {
            return new Student(in);
        }

        @Override
        public Student[] newArray(int size) {
            return new Student[size];
        }
    };

    // Getters and Setters
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
    public String getSubmissionDate() { return submissionDate; }
    public void setSubmissionDate(String submissionDate) { this.submissionDate = submissionDate; }
    public String getCallStatus() { return callStatus; }
    public void setCallStatus(String callStatus) { this.callStatus = callStatus; }
    public String getLastCallDate() { return lastCallDate; }
    public void setLastCallDate(String lastCallDate) { this.lastCallDate = lastCallDate; }
    public String getLastUpdatedDate() { return lastUpdatedDate; }
    public void setLastUpdatedDate(String lastUpdatedDate) { this.lastUpdatedDate = lastUpdatedDate; }
    public String getFirebasePushDate() { return firebasePushDate; }
    public void setFirebasePushDate(String firebasePushDate) { this.firebasePushDate = firebasePushDate; }
    public boolean getIsInterested() { return isInterested; }
    public void setIsInterested(boolean isInterested) { this.isInterested = isInterested; }
    public boolean isAdmitted() { return isAdmitted; }
    public void setAdmitted(boolean isAdmitted) { this.isAdmitted = isAdmitted; }

    public String getCollection() { return collection; } // Added getter for collection
    public void setCollection(String collection) { this.collection = collection; } // Added setter for collection

    @Override
    public int describeContents() {
        return 0; // No special contents
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(mobile);
        dest.writeString(email);
        dest.writeString(state);
        dest.writeString(city);
        dest.writeString(interestedCountry);
        dest.writeString(hasNeetScore);
        dest.writeString(neetScore);
        dest.writeString(hasPassport);
        dest.writeString(submissionDate);
        dest.writeString(callStatus);
        dest.writeString(lastCallDate);
        dest.writeString(lastUpdatedDate);
        dest.writeString(firebasePushDate);
        dest.writeByte((byte) (isInterested ? 1 : 0)); // Boolean to byte
        dest.writeByte((byte) (isAdmitted ? 1 : 0));   // Boolean to byte
        dest.writeString(collection); // Added: Write collection to Parcel
    }
}

