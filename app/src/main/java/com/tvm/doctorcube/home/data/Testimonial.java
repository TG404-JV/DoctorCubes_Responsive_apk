package com.tvm.doctorcube.home.data;

public class Testimonial {
    private int avatarResId;
    private String name;
    private String testimonial;
    private String flagResId;
    private String university;  // New field
    private String batch;       // New field
    private float rating;       // New field

    public Testimonial(int avatarResId, String name, String testimonial, String flagResId, String university, String batch, float rating) {
        this.avatarResId = avatarResId;
        this.name = name;
        this.testimonial = testimonial;
        this.flagResId = flagResId;
        this.university = university;
        this.batch = batch;
        this.rating = rating;
    }

    public int getAvatarResId() {
        return avatarResId;
    }

    public String getName() {
        return name;
    }

    public String getTestimonial() {
        return testimonial;
    }

    public String getFlagResId() {
        return flagResId;
    }

    public String getUniversity() {
        return university;
    }

    public String getBatch() {
        return batch;
    }

    public float getRating() {
        return rating;
    }
}