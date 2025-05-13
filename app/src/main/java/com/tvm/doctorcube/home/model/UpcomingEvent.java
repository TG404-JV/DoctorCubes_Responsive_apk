package com.tvm.doctorcube.home.model;


public class UpcomingEvent {
    private String title;
    private String date;
    private String time;
    private String location;
    private String category;
    private boolean isPremium;
    private boolean isBookmarked;
    private String imageUrl;

    // Default constructor required for Firebase
    public UpcomingEvent() {}

    public UpcomingEvent(String title, String date, String time, String location, String category, boolean isPremium, boolean isBookmarked, String imageUrl) {
        this.title = title;
        this.date = date;
        this.time = time;
        this.location = location;
        this.category = category;
        this.isPremium = isPremium;
        this.isBookmarked = isBookmarked;
        this.imageUrl = imageUrl;
    }

    // Getters and setters
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public boolean isPremium() { return isPremium; }
    public void setPremium(boolean premium) { isPremium = premium; }
    public boolean isBookmarked() { return isBookmarked; }
    public void setBookmarked(boolean bookmarked) { isBookmarked = bookmarked; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
}
