package com.tvm.doctorcube.customview;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.card.MaterialCardView;
import com.tvm.doctorcube.R;

public class CustomBottomNavigationView extends MaterialCardView {

    private LinearLayout navHome, navStudy, navSettings;
    private LottieAnimationView navHomeIcon, navStudyIcon, navSettingsIcon;
    private TextView navHomeLabel, navStudyLabel, navSettingsLabel;
    private int selectedItemPosition = 0; // Default to home
    private OnNavigationItemSelectedListener listener;

    public CustomBottomNavigationView(@NonNull Context context) {
        super(context);
        init(context, null);
    }

    public CustomBottomNavigationView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public CustomBottomNavigationView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, @Nullable AttributeSet attrs) {
        LayoutInflater.from(context).inflate(R.layout.view_custom_bottom_navigation, this, true);

        // Initialize views
        navHome = findViewById(R.id.nav_home);
        navStudy = findViewById(R.id.nav_study);
        navSettings = findViewById(R.id.nav_settings);

        navHomeIcon = findViewById(R.id.nav_home_icon);
        navStudyIcon = findViewById(R.id.nav_study_icon);
        navSettingsIcon = findViewById(R.id.nav_settings_icon);

        navHomeLabel = findViewById(R.id.nav_home_label);
        navStudyLabel = findViewById(R.id.nav_study_label);
        navSettingsLabel = findViewById(R.id.nav_settings_label);

        // Set accessibility content descriptions
        setContentDescriptions();

        // Set up click listeners
        setupClickListeners();

        // Apply initial state
        setSelectedItem(selectedItemPosition);

        // Handle custom attributes (if needed)
        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CustomBottomNavigationView, 0, 0);
            selectedItemPosition = a.getInt(R.styleable.CustomBottomNavigationView_selectedItem, 0);
            a.recycle();
            setSelectedItem(selectedItemPosition);
        }
    }

    private void setContentDescriptions() {
        if (navHome != null) navHome.setContentDescription(getContext().getString(R.string.duration_icon));
        if (navStudy != null) navStudy.setContentDescription(getContext().getString(R.string.duration_icon));
        if (navSettings != null) navSettings.setContentDescription(getContext().getString(R.string.duration_icon));
    }

    private void setupClickListeners() {
        LinearLayout[] navItems = {navHome, navStudy, navSettings};
        for (int i = 0; i < navItems.length; i++) {
            final int position = i;
            if (navItems[i] != null) {
                navItems[i].setOnClickListener(v -> {
                    if (selectedItemPosition != position) {
                        setSelectedItem(position);
                        if (listener != null) {
                            listener.onNavigationItemSelected(position);
                        }
                    }
                });
            }
        }
    }

    public void setSelectedItem(int position) {
        LottieAnimationView[] icons = {navHomeIcon, navStudyIcon, navSettingsIcon};
        TextView[] labels = {navHomeLabel, navStudyLabel, navSettingsLabel};

        for (int i = 0; i < icons.length; i++) {
            if (icons[i] == null || labels[i] == null) continue;
            if (i == position) {
                icons[i].playAnimation();
                icons[i].setColorFilter(ContextCompat.getColor(getContext(), R.color.primary_color));
                labels[i].setTextColor(ContextCompat.getColor(getContext(), R.color.primary_color));
            } else {
                icons[i].cancelAnimation();
                icons[i].setProgress(0f);
                icons[i].clearColorFilter();
                labels[i].setTextColor(ContextCompat.getColor(getContext(), R.color.text_primary_color));
            }
        }
        selectedItemPosition = position;
    }

    public void setOnNavigationItemSelectedListener(OnNavigationItemSelectedListener listener) {
        this.listener = listener;
    }

    public interface OnNavigationItemSelectedListener {
        void onNavigationItemSelected(int position);
    }
}