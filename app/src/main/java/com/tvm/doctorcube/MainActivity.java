package com.tvm.doctorcube;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.airbnb.lottie.LottieAnimationView;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private LottieAnimationView callButton;
    private LottieAnimationView[] lottieAnimationViews;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Toolbar
        toolbar = findViewById(R.id.toolbar);
        if (toolbar == null) {
            throw new IllegalStateException("Toolbar not found in activity_main.xml");
        }
        setSupportActionBar(toolbar);

        // Apply animation to app logo click
        View appLogo = findViewById(R.id.app_logo);
        appLogo.setOnClickListener(v -> {
            applySelectionAnimation(v);
            setActiveNavigationItem(2); // Settings
            loadFragment(new SettingsFragment());
        });

        // Initialize call button
        callButton = toolbar.findViewById(R.id.call_button);
        if (callButton == null) {
            CustomToast.showToast(this, "Call button not found in toolbar");
            return;
        }

        // Set up Toolbar functionality
        setupToolbar();

        TextView app_title = findViewById(R.id.app_title);
        app_title.setText(getString(R.string.app_name));

        // Initialize custom navigation
        setupCustomNavigation();

        // Load the home fragment by default
        loadFragment(new HomeFragment());
        setActiveNavigationItem(0); // Home
    }

    private void setupToolbar() {
        callButton.setOnClickListener(v -> {
            applySelectionAnimation(v);
            SocialActions openMedia = new SocialActions();
            openMedia.makeDirectCall(this);
        });

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);

            Objects.requireNonNull(toolbar.getNavigationIcon()).setColorFilter(
                    ContextCompat.getColor(this, android.R.color.white),
                    PorterDuff.Mode.SRC_IN
            );

            toolbar.setNavigationOnClickListener(v -> {
                applySelectionAnimation(v);
                onBackPressed();
            });
        }
    }

    private void setupCustomNavigation() {
        lottieAnimationViews = new LottieAnimationView[]{
                findViewById(R.id.nav_home_icon),
                findViewById(R.id.nav_study_icon),
                findViewById(R.id.nav_settings_icon)
        };
        LinearLayout[] navItems = new LinearLayout[]{
                findViewById(R.id.nav_home),
                findViewById(R.id.nav_study),
                findViewById(R.id.nav_settings)
        };

        for (int i = 0; i < navItems.length; i++) {
            final int position = i;
            navItems[i].setOnClickListener(v -> {
                applySelectionAnimation(v);
                setActiveNavigationItem(position);
                Fragment fragment = null;
                switch (position) {
                    case 0:
                        fragment = new HomeFragment();
                        toolbar.setTitle("DoctorCubes");
                        break;
                    case 1:
                        fragment = new StudyMaterialFragment();
                        toolbar.setTitle("Study Material");
                        break;
                    case 2:
                        fragment = new SettingsFragment();
                        toolbar.setTitle("Settings");
                        break;
                }
                loadFragment(fragment);
            });
        }

        // Set initial state
        lottieAnimationViews[0].playAnimation();
    }

    public void setActiveNavigationItem(int position) {
        for (int i = 0; i < lottieAnimationViews.length; i++) {
            if (i == position) {
                lottieAnimationViews[i].playAnimation();
                // Apply tint to selected Lottie icon
                lottieAnimationViews[i].setColorFilter(
                        ContextCompat.getColor(this, R.color.primary_color),
                        PorterDuff.Mode.SRC_IN
                );
            } else {
                lottieAnimationViews[i].cancelAnimation();
                lottieAnimationViews[i].setProgress(0f);
                // Remove tint from unselected Lottie icons
                lottieAnimationViews[i].clearColorFilter();
            }
        }
    }

    private void applySelectionAnimation(View view) {
        view.setScaleX(0.9f);
        view.setScaleY(0.9f);
        view.animate()
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(300)
                .setInterpolator(new OvershootInterpolator(1.5f))
                .start();
    }

    private void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        transaction.setCustomAnimations(
                R.anim.fragment_fade_enter,
                R.anim.fragment_fade_exit,
                R.anim.fragment_fade_enter,
                R.anim.fragment_fade_exit
        );

        transaction.replace(R.id.frame_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onBackPressed() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment currentFragment = fragmentManager.findFragmentById(R.id.frame_container);

        if (currentFragment instanceof HomeFragment) {
            finishAffinity();
        } else {
            super.onBackPressed();
            updateNavigationSelection();
        }
    }

    private void updateNavigationSelection() {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.frame_container);

        if (currentFragment instanceof HomeFragment) {
            setActiveNavigationItem(0);
            toolbar.setTitle("DoctorCubes");
        } else if (currentFragment instanceof StudyMaterialFragment) {
            setActiveNavigationItem(1);
            toolbar.setTitle("Study Material");
        } else if (currentFragment instanceof SettingsFragment) {
            setActiveNavigationItem(2);
            toolbar.setTitle("Settings");
        }
    }

    public Toolbar getToolbar() {
        return toolbar;
    }
}