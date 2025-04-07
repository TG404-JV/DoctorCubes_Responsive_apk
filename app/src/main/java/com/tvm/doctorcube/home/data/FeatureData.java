package com.tvm.doctorcube.home.data;

import android.content.Context;

import com.tvm.doctorcube.R;
import com.tvm.doctorcube.home.model.Feature;

import java.util.ArrayList;
import java.util.List;

public class FeatureData {

    // Singleton instance
    private static FeatureData instance;

    // Get singleton instance
    public static FeatureData getInstance() {
        if (instance == null) {
            instance = new FeatureData();
        }
        return instance;
    }

    // Get feature list
    public List<Feature> getFeatures(Context context) {
        List<Feature> featureList = new ArrayList<>();

        // Use the feature type constants from Feature class instead of R.id.card_*
        featureList.add(new Feature(
                Feature.TYPE_UNIVERSITY_LISTINGS,
                R.drawable.icon_university,
                context.getResources().getColor(R.color.university_icon_bg),
                "Top University Listings",
                "Browse top medical universities by country"
        ));

        featureList.add(new Feature(
                Feature.TYPE_SCHOLARSHIP,
                R.drawable.icon_scholarship,
                context.getResources().getColor(R.color.scholarship_icon_bg),
                "Scholarship Assistance",
                "Find and apply for medical scholarships"
        ));

        featureList.add(new Feature(
                Feature.TYPE_VISA_ADMISSION,
                R.drawable.icon_visa,
                context.getResources().getColor(R.color.visa_icon_bg),
                "Visa and Admission Guidance",
                "Complete support for visa and admission process"
        ));

        featureList.add(new Feature(
                Feature.TYPE_TRACKING,
                R.drawable.icon_tracking,
                context.getResources().getColor(R.color.tracking_icon_bg),
                "Application Tracking",
                "Track your application status in real-time"
        ));

        featureList.add(new Feature(
                Feature.TYPE_SUPPORT,
                R.drawable.icon_support,
                context.getResources().getColor(R.color.support_icon_bg),
                "24/7 Student Support",
                "Round-the-clock assistance for all your queries"
        ));

        return featureList;
    }
}