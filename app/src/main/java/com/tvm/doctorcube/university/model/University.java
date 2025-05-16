package com.tvm.doctorcube.university.model;

import java.io.Serializable;

public class University implements Serializable {
    private String id; // Unique identifier
    private String name;
    private String city;
    private String country;
    private int logoResourceId; // Used as imageResourceId in JSON
    private int bannerResourceId; // Same as logoResourceId for simplicity
    private int flagResourceId; // Derived from country
    private String description;
    private String established;
    private String ranking;
    private String grade; // Maps to rating in JSON
    private String address;
    private String phone;
    private String email;
    private String admissionRequirements;
    private String courseName; // Maps to program in JSON
    private String degreeType; // Maps to degree in JSON
    private String duration;
    private String intake;
    private String language; // Maps to medium in JSON
    private String universityType; // Maps to type in JSON
    private String worldRanking;
    private String scholarshipInfo; // Maps to scholarship in JSON
    private String field; // Not in JSON, use default

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }
    public int getLogoResourceId() { return logoResourceId; }
    public void setLogoResourceId(int logoResourceId) { this.logoResourceId = logoResourceId; }
    public int getBannerResourceId() { return bannerResourceId; }
    public void setBannerResourceId(int bannerResourceId) { this.bannerResourceId = bannerResourceId; }
    public int getFlagResourceId() { return flagResourceId; }
    public void setFlagResourceId(int flagResourceId) { this.flagResourceId = flagResourceId; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getEstablished() { return established; }
    public void setEstablished(String established) { this.established = established; }
    public String getRanking() { return ranking; }
    public void setRanking(String ranking) { this.ranking = ranking; }
    public String getGrade() { return grade; }
    public void setGrade(String grade) { this.grade = grade; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getAdmissionRequirements() { return admissionRequirements; }
    public void setAdmissionRequirements(String admissionRequirements) { this.admissionRequirements = admissionRequirements; }
    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }
    public String getDegreeType() { return degreeType; }
    public void setDegreeType(String degreeType) { this.degreeType = degreeType; }
    public String getDuration() { return duration; }
    public void setDuration(String duration) { this.duration = duration; }
    public String getIntake() { return intake; }
    public void setIntake(String intake) { this.intake = intake; }
    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }
    public String getUniversityType() { return universityType; }
    public void setUniversityType(String universityType) { this.universityType = universityType; }
    public String getWorldRanking() { return worldRanking; }
    public void setWorldRanking(String worldRanking) { this.worldRanking = worldRanking; }
    public String getScholarshipInfo() { return scholarshipInfo; }
    public void setScholarshipInfo(String scholarshipInfo) { this.scholarshipInfo = scholarshipInfo; }
    public String getField() { return field; }
    public void setField(String field) { this.field = field; }
    // For UniversityListAdapter
    public String getLocation() { return city; }
}