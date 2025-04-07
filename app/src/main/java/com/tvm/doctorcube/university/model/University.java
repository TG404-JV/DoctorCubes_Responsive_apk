package com.tvm.doctorcube.university.model;

public class University {
    private String id;
    private String name;
    private String location;
    private String country;
    private String courseName;
    private String degreeType;
    private String duration;
    private String grade;
    private String intake;
    private String language;
    private String universityType; // Public/Private
    private int bannerResourceId;
    private int logoResourceId;
    private int flagResourceId;
    private String field; // e.g., "medical"
    private String ranking; // e.g., "Top 500"
    private String scholarshipInfo;

    public University(String id, String name, String location, String country,
                      String courseName, String degreeType, String duration,
                      String grade, String intake, String language,
                      String universityType, int bannerResourceId, int logoResourceId,
                      int flagResourceId, String field, String ranking, String scholarshipInfo) {
        this.id = id;
        this.name = name;
        this.location = location;
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

    public University()
    {

    }

    // Getters
    public String getId() { return id; }
    public  String getName() { return name; }
    public String getLocation() { return location; }
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
}