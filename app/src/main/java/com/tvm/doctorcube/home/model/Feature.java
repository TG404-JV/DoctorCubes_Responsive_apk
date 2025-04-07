package com.tvm.doctorcube.home.model;

public class Feature {
    // Static constants for feature types
    public static final int TYPE_UNIVERSITY_LISTINGS = 1;
    public static final int TYPE_SCHOLARSHIP = 2;
    public static final int TYPE_VISA_ADMISSION = 3;
    public static final int TYPE_TRACKING = 4;
    public static final int TYPE_SUPPORT = 5;

    private int iconResource;
    private int iconBackgroundColor;
    private String title;
    private String description;
    private int id; // This is now the feature type

    public Feature(int id, int iconResource, int iconBackgroundColor, String title, String description) {
        this.id = id;
        this.iconResource = iconResource;
        this.iconBackgroundColor = iconBackgroundColor;
        this.title = title;
        this.description = description;
    }

    // Getters
    public int getIconResource() { return iconResource; }
    public int getIconBackgroundColor() { return iconBackgroundColor; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public int getId() { return id; }
}