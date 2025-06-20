package com.tvm.doctorcube.university.model;

import java.io.Serializable;

public class University implements Serializable {
    private String id;
    private String name;
    private String city;
    private String country;
    private String courseName; // Maps to JSON "program"
    private String degreeType; // Maps to JSON "degree"
    private String duration;
    private String grade; // Maps to JSON "rating"
    private String intake;
    private String language; // Maps to JSON "medium"
    private String universityType; // Maps to JSON "type"
    private int bannerResourceId; // Maps to JSON "imageResourceId"
    private int logoResourceId;
    private int flagResourceId;
    private String field; // Maps to JSON "category"
    private String ranking; // Maps to JSON "worldRanking" for ranking tag
    private String scholarshipInfo; // Maps to JSON "scholarship"
    private String description;
    private String established;
    private String detailedRanking; // Maps to JSON "ranking"
    private String address;
    private String phone;
    private String email;
    private String admissionRequirements;

    public University(String id, String name, String city, String country, String courseName,
                      String degreeType, String duration, String grade, String intake,
                      String language, String universityType, int bannerResourceId,
                      int logoResourceId, int flagResourceId, String field,
                      String ranking, String scholarshipInfo) {
        this.id = id;
        this.name = name;
        this.city = city;
        this.country = country;
        this.courseName = courseName;
        this.degreeType = degreeType;
        this.duration = duration;
        this.grade = grade;
        this.intake = intake;
        this.language = language;
        this.universityType = universityType;
        this.bannerResourceId = bannerResourceId;
        this.logoResourceId = logoResourceId;
        this.flagResourceId = flagResourceId;
        this.field = field;
        this.ranking = ranking;
        this.scholarshipInfo = scholarshipInfo;
    }

    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public String getCity() { return city; }
    public String getCountry() { return country; }
    public String getCourseName() { return courseName; }
    public String getDegreeType() { return degreeType; }
    public String getDuration() { return duration; }
    public String getGrade() { return grade; }
    public String getIntake() { return intake; }
    public String getLanguage() { return language; }
    public String getUniversityType() { return universityType; }
    public int getBannerResourceId() { return bannerResourceId; }
    public int getLogoResourceId() { return logoResourceId; }
    public int getFlagResourceId() { return flagResourceId; }
    public String getField() { return field; }
    public String getRanking() { return ranking; }
    public String getScholarshipInfo() { return scholarshipInfo; }
    public String getDescription() { return description; }
    public String getEstablished() { return established; }
    public String getDetailedRanking() { return detailedRanking; }
    public String getAddress() { return address; }
    public String getPhone() { return phone; }
    public String getEmail() { return email; }
    public String getAdmissionRequirements() { return admissionRequirements; }

    // Setters for additional fields
    public void setDescription(String description) { this.description = description; }
    public void setEstablished(String established) { this.established = established; }
    public void setDetailedRanking(String detailedRanking) { this.detailedRanking = detailedRanking; }
    public void setAddress(String address) { this.address = address; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setEmail(String email) { this.email = email; }
    public void setAdmissionRequirements(String admissionRequirements) { this.admissionRequirements = admissionRequirements; }
}