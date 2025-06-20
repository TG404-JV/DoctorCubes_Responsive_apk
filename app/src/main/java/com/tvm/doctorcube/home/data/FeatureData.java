package com.tvm.doctorcube.home.data;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.tvm.doctorcube.R;
import com.tvm.doctorcube.home.model.Feature;

import java.util.ArrayList;
import java.util.List;

/**
 * Singleton class to provide a list of Feature objects for the home screen's RecyclerView.
 */
public class FeatureData {

    // Singleton instance (eager initialization for thread safety)
    private static final FeatureData instance = new FeatureData();

    /**
     * Gets the singleton instance of FeatureData.
     *
     * @return The singleton FeatureData instance.
     */
    @NonNull
    public static FeatureData getInstance() {
        return instance;
    }

    // Private constructor to prevent direct instantiation
    private FeatureData() {
    }

    /**
     * Retrieves a list of Feature objects to be displayed in the RecyclerView.
     *
     * @param context The context used to access resources (e.g., drawables, colors).
     * @return A list of Feature objects.
     * @throws IllegalStateException if required resources are missing or invalid.
     */
    @NonNull
    public List<Feature> getFeatures(@NonNull Context context) {
        List<Feature> featureList = new ArrayList<>();

        try {
            // Feature 1: University Listings
            featureList.add(new Feature(
                    Feature.TYPE_UNIVERSITY_LISTINGS,
                    R.drawable.icon_university,
                    ContextCompat.getColor(context, R.color.university_icon_bg),
                    "Top University Listings",
                    "Browse top medical universities by country"
            ));

            // Feature 2: Scholarship Assistance
            featureList.add(new Feature(
                    Feature.TYPE_SCHOLARSHIP,
                    R.drawable.icon_scholarship,
                    ContextCompat.getColor(context, R.color.scholarship_icon_bg),
                    "Scholarship Assistance",
                    "Find and apply for medical scholarships"
            ));

            // Feature 3: Visa and Admission Guidance
            featureList.add(new Feature(
                    Feature.TYPE_VISA_ADMISSION,
                    R.drawable.icon_visa,
                    ContextCompat.getColor(context, R.color.visa_icon_bg),
                    "Visa and Admission Guidance",
                    "Complete support for visa and admission process"
            ));

            // Feature 4: Application Tracking
            featureList.add(new Feature(
                    Feature.TYPE_TRACKING,
                    R.drawable.icon_tracking,
                    ContextCompat.getColor(context, R.color.tracking_icon_bg),
                    "Application Tracking",
                    "Track your application status in real-time"
            ));

            // Feature 5: Student Support
            featureList.add(new Feature(
                    Feature.TYPE_SUPPORT,
                    R.drawable.icon_support,
                    ContextCompat.getColor(context, R.color.support_icon_bg),
                    "24/7 Student Support",
                    "Round-the-clock assistance for all your queries"
            ));
        } catch (Exception e) {
            // Handle resource or constructor errors (e.g., missing drawable/color, invalid feature type)
            throw new IllegalStateException("Failed to create feature list: " + e.getMessage(), e);
        }

        return featureList;
    }
}