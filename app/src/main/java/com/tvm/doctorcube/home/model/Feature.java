package com.tvm.doctorcube.home.model;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.Objects;

/**
 * Represents a feature item displayed in the home screen's RecyclerView.
 */
public class Feature implements Serializable {
    // Static constants for feature types
    public static final int TYPE_UNIVERSITY_LISTINGS = 1;
    public static final int TYPE_SCHOLARSHIP = 2;
    public static final int TYPE_VISA_ADMISSION = 3;
    public static final int TYPE_TRACKING = 4;
    public static final int TYPE_SUPPORT = 5;

    private final int id; // Feature type (e.g., TYPE_UNIVERSITY_LISTINGS)
    @DrawableRes
    private final int iconResource; // Drawable resource ID for the feature icon
    private final int iconBackgroundColor; // Resolved color value for icon background tint
    @NonNull
    private final String title; // Feature title
    @NonNull
    private final String description; // Feature description

    /**
     * Constructor for Feature.
     *
     * @param id                The feature type (e.g., TYPE_UNIVERSITY_LISTINGS).
     * @param iconResource      The drawable resource ID for the icon.
     * @param iconBackgroundColor The resolved color value for the icon's background tint.
     * @param title             The title of the feature (non-null).
     * @param description       The description of the feature (non-null).
     * @throws IllegalArgumentException if id is invalid or title/description is null.
     */
    public Feature(int id, @DrawableRes int iconResource, int iconBackgroundColor,
                   @NonNull String title, @NonNull String description) {
        validateFeatureType(id);
        this.id = id;
        this.iconResource = iconResource;
        this.iconBackgroundColor = iconBackgroundColor;
        this.title = Objects.requireNonNull(title, "Title cannot be null");
        this.description = Objects.requireNonNull(description, "Description cannot be null");
    }

    /**
     * Validates that the provided feature type is one of the defined constants.
     *
     * @param id The feature type to validate.
     * @throws IllegalArgumentException if the id is invalid.
     */
    private void validateFeatureType(int id) {
        if (id != TYPE_UNIVERSITY_LISTINGS &&
                id != TYPE_SCHOLARSHIP &&
                id != TYPE_VISA_ADMISSION &&
                id != TYPE_TRACKING &&
                id != TYPE_SUPPORT) {
            throw new IllegalArgumentException("Invalid feature type: " + id);
        }
    }

    // Getters
    public int getId() {
        return id;
    }

    @DrawableRes
    public int getIconResource() {
        return iconResource;
    }

    public int getIconBackgroundColor() {
        return iconBackgroundColor;
    }

    @NonNull
    public String getTitle() {
        return title;
    }

    @NonNull
    public String getDescription() {
        return description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Feature feature = (Feature) o;
        return id == feature.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Feature{id=" + id + ", title='" + title + "'}";
    }
}