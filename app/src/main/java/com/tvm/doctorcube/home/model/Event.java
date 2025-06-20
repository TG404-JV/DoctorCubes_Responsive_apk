package com.tvm.doctorcube.home.model;

public class Event {
    private int imageResId;
    private String date;
    private String title;
    private String location;
    private String attendees;

    public Event(int imageResId, String date, String title, String location, String attendees) {
        this.imageResId = imageResId;
        this.date = date;
        this.title = title;
        this.location = location;
        this.attendees = attendees;
    }

    public int getImageResId() { return imageResId; }
    public String getDate() { return date; }
    public String getTitle() { return title; }
    public String getLocation() { return location; }
    public String getAttendees() { return attendees; }
}
