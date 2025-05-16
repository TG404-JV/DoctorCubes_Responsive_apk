package com.tvm.doctorcube;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.airbnb.lottie.LottieAnimationView;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private LottieAnimationView callButton;
    private LottieAnimationView[] lottieAnimationViews;
    private NavController navController;
    private boolean doubleBackToExitPressedOnce = false;

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

        // Setup Navigation Component
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        if (navHostFragment == null) {
            throw new IllegalStateException("NavHostFragment not found in R.id.nav_host_fragment");
        }
        navController = navHostFragment.getNavController();
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.homeFragment, R.id.universityFragment, R.id.settingsHome
        ).build();
        NavigationUI.setupWithNavController(toolbar, navController, appBarConfiguration);

        // Setup back press handling
        setupBackPressHandling();

        // Apply animation to app logo click
        View appLogo = findViewById(R.id.app_logo);
        if (appLogo != null) {
            appLogo.setOnClickListener(v -> {
                if (navController.getCurrentDestination().getId() != R.id.settingsHome) {
                    applySelectionAnimation(v);
                    setActiveNavigationItem(2);
                    navController.navigate(R.id.settingsHome);
                }
            });
        }

        // Initialize call button
        callButton = toolbar.findViewById(R.id.call_button);
        if (callButton == null) {
            CustomToast.showToast(this, "Call button not found in toolbar");
        } else {
            setupToolbar();
        }

        // Set app title
        TextView appTitle = findViewById(R.id.app_title);
        if (appTitle != null) {
            appTitle.setText(getString(R.string.app_name));
        }

        // Initialize custom navigation
        setupCustomNavigation();

        // Update toolbar title based on destination
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            if (destination.getId() == R.id.homeFragment) {
                toolbar.setTitle("DoctorCubes");
                setActiveNavigationItem(0);
            } else if (destination.getId() == R.id.universityFragment) {
                String country = arguments != null ? arguments.getString("COUNTRY_NAME", "All") : "All";
                toolbar.setTitle(country.equals("All") ? "All Universities" : "Universities in " + country);
                setActiveNavigationItem(1);
            } else if (destination.getId() == R.id.settingsHome) {
                toolbar.setTitle("Settings");
                setActiveNavigationItem(2);
            } else if (destination.getId() == R.id.universityDetailsFragment) {
                String universityName = arguments != null ? arguments.getString("UNIVERSITY_NAME", "") : "";
                toolbar.setTitle(universityName);
            } else if (destination.getId() == R.id.studyMaterialFragment) {
                toolbar.setTitle("Study Material");
            }
        });
    }

    private void setupBackPressHandling() {
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (navController.getCurrentDestination().getId() == R.id.homeFragment) {
                    if (doubleBackToExitPressedOnce) {
                        finish();
                    } else {
                        doubleBackToExitPressedOnce = true;
                        Toast.makeText(MainActivity.this, "Press back again to exit", Toast.LENGTH_SHORT).show();
                        new Handler(Looper.getMainLooper()).postDelayed(() -> doubleBackToExitPressedOnce = false, 2000);
                    }
                } else {
                    navController.navigateUp();
                }
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);
    }

    private void setupToolbar() {
        callButton.setOnClickListener(v -> {
            applySelectionAnimation(v);
            SocialActions openMedia = new SocialActions();
            openMedia.makeDirectCall(this);
        });

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setHomeButtonEnabled(true);

            if (toolbar.getNavigationIcon() != null) {
                toolbar.getNavigationIcon().setColorFilter(
                        ContextCompat.getColor(this, android.R.color.white),
                        PorterDuff.Mode.SRC_IN
                );
            }
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
                int currentDestination = navController.getCurrentDestination().getId();
                int targetDestination;
                switch (position) {
                    case 0:
                        targetDestination = R.id.homeFragment;
                        break;
                    case 1:
                        targetDestination = R.id.studyMaterialFragment;
                        break;
                    case 2:
                        targetDestination = R.id.settingsHome;
                        break;
                    default:
                        return;
                }
                if (currentDestination != targetDestination) {
                    applySelectionAnimation(v);
                    setActiveNavigationItem(position);
                    navController.navigate(targetDestination);
                }
            });
        }

        // Set initial state
        if (lottieAnimationViews[0] != null) {
            lottieAnimationViews[0].playAnimation();
        }
    }

    public void setActiveNavigationItem(int position) {
        for (int i = 0; i < lottieAnimationViews.length; i++) {
            if (lottieAnimationViews[i] == null) continue;
            if (i == position) {
                lottieAnimationViews[i].playAnimation();
                lottieAnimationViews[i].setColorFilter(
                        ContextCompat.getColor(this, R.color.primary_color),
                        PorterDuff.Mode.SRC_IN
                );
            } else {
                lottieAnimationViews[i].cancelAnimation();
                lottieAnimationViews[i].setProgress(0f);
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

    public Toolbar getToolbar() {
        return toolbar;
    }

    @Override
    public boolean onSupportNavigateUp() {
        return navController.navigateUp() || super.onSupportNavigateUp();
    }
}