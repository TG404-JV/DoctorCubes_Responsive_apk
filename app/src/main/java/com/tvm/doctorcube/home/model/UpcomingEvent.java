package com.tvm.doctorcube.home.model;

public class UpcomingEvent {
    private String thisMonth;
    private String title;
    private String category;
    private String date;
    private String time;
    private String location;
    private String attendees;
    private String imageUrl;
    private boolean premium;
    private boolean featured;

    // Default constructor for Firestore
    public UpcomingEvent() {}

    public UpcomingEvent(String thisMonth, String title, String category, String date, String time,
                         String location, String attendees, String imageUrl, boolean premium, boolean featured) {
        this.thisMonth = thisMonth;
        this.title = title;
        this.category = category;
        this.date = date;
        this.time = time;
        this.location = location;
        this.attendees = attendees;
        this.imageUrl = imageUrl;
        this.premium = premium;
        this.featured = featured;
    }

    public UpcomingEvent(String title, String date, String time, String location, String category, String imageUrl, String attendees, boolean isPremium, boolean isFeatured) {
        this.title = title;
        this.date = date;
        this.time = time;
        this.location = location;
        this.category = category;
        this.imageUrl = imageUrl;
        this.attendees = attendees;
        this.premium = isPremium;
        this.featured = isFeatured;
    }


    // Getters and setters
    public String getThisMonth() { return thisMonth; }
    public void setThisMonth(String thisMonth) { this.thisMonth = thisMonth; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public String getAttendees() { return attendees; }
    public void setAttendees(String attendees) { this.attendees = attendees; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public boolean isPremium() { return premium; }
    public void setPremium(boolean premium) { this.premium = premium; }
    public boolean isFeatured() { return featured; }
    public void setFeatured(boolean featured) { this.featured = featured; }
}