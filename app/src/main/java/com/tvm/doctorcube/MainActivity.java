package com.tvm.doctorcube;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.gauravk.bubblenavigation.BubbleNavigationConstraintView;

public class MainActivity extends AppCompatActivity {

    private BubbleNavigationConstraintView bubbleNavigation;
    private Toolbar toolbar;
    private ImageView callButton;
    private TextView app_title;

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
        appLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                applySelectionAnimation(v);  // Apply animation to the logo
                animateNavigationView();     // Animate navigation view
                loadFragment(new SettingsFragment(), true);
            }
        });

        // Initialize call button
        callButton = toolbar.findViewById(R.id.call_button);
        if (callButton == null) {
            CustomToast.showToast(this, "Call button not found in toolbar");
            return;
        }

        // Set up Toolbar functionality
        setupToolbar();

        app_title = findViewById(R.id.app_title);
        app_title.setText("DoctorCubes");

        // Initialize Bubble Navigation
        bubbleNavigation = findViewById(R.id.bubbleNavigation);
        bubbleNavigation.setCurrentActiveItem(0);

        // Load the home fragment by default
        loadFragment(new HomeFragment(), true);

        // Handle navigation selection
        bubbleNavigation.setNavigationChangeListener((view, position) -> {
            applySelectionAnimation(view);
            animateNavigationView();     // Animate navigation view

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

            if (fragment != null) {
                loadFragment(fragment, true);
            }
        });
    }

    private void setupToolbar() {
        // Call button click with animation
        callButton.setOnClickListener(v -> {
            applySelectionAnimation(v);  // Apply animation to the call button
            SocialActions openMedia = new SocialActions();
            openMedia.makeDirectCall(this);
        });

        // Add animation to navigation icon if it exists and set white tint
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);

            // Set white tint to the back button (navigation icon)
            toolbar.getNavigationIcon().setColorFilter(
                    ContextCompat.getColor(this, android.R.color.white),
                    PorterDuff.Mode.SRC_IN
            );

            toolbar.setNavigationOnClickListener(v -> {
                applySelectionAnimation(v);  // Apply animation to the navigation icon
                animateNavigationView();     // Animate navigation view
                onBackPressed();  // Default behavior is to go back
            });
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

    private void animateNavigationView() {
        // Apply animation to the whole navigation view
        bubbleNavigation.setAlpha(0.7f);
        bubbleNavigation.setScaleX(0.95f);
        bubbleNavigation.setScaleY(0.95f);
        bubbleNavigation.animate()
                .alpha(1f)
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(350)
                .setInterpolator(new OvershootInterpolator(1.2f))
                .start();
    }

    private void loadFragment(Fragment fragment, boolean withAnimation) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        if (withAnimation) {
            transaction.setCustomAnimations(
                    R.anim.fragment_fade_enter,
                    R.anim.fragment_fade_exit,
                    R.anim.fragment_fade_enter,
                    R.anim.fragment_fade_exit
            );
        }

        transaction.replace(R.id.frame_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onBackPressed() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment currentFragment = fragmentManager.findFragmentById(R.id.frame_container);

        if (currentFragment instanceof HomeFragment) {
            finishAffinity(); // Use finishAffinity() to close the app
        } else {
            animateNavigationView(); // Animate navigation view when back is pressed
            super.onBackPressed();
            updateNavigationSelection();
        }
    }

    private void updateNavigationSelection() {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.frame_container);

        if (currentFragment instanceof HomeFragment) {
            bubbleNavigation.setCurrentActiveItem(0);
            toolbar.setTitle("DoctorCubes");
        } else if (currentFragment instanceof StudyMaterialFragment) {
            bubbleNavigation.setCurrentActiveItem(1);
            toolbar.setTitle("Study Material");
        } else if (currentFragment instanceof SettingsFragment) {
            bubbleNavigation.setCurrentActiveItem(2);
            toolbar.setTitle("Settings");
        }
    }

    public BubbleNavigationConstraintView getBubbleNavigation() {
        return bubbleNavigation;
    }

    public Toolbar getToolbar() {
        return toolbar;
    }
}