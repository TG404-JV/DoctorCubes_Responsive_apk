package com.tvm.doctorcube;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.airbnb.lottie.LottieAnimationView;
import com.tvm.doctorcube.customview.CustomBottomNavigationView;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private LottieAnimationView callButton;
    private NavController navController;
    private boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Handle window insets for edge-to-edge display
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

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
                if (Objects.requireNonNull(navController.getCurrentDestination()).getId() != R.id.settingsHome) {
                    applySelectionAnimation(v);
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

        // Update toolbar title and navigation item based on destination
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            CustomBottomNavigationView bottomNav = findViewById(R.id.custom_bottom_navigation);
            if (destination.getId() == R.id.homeFragment) {
                toolbar.setTitle("DoctorCubes");
                if (bottomNav != null) bottomNav.setSelectedItem(0);
            } else if (destination.getId() == R.id.universityFragment) {
                String country = arguments != null ? arguments.getString("COUNTRY_NAME", "All") : "All";
                toolbar.setTitle(country.equals("All") ? "All Universities" : "Universities in " + country);
                if (bottomNav != null) bottomNav.setSelectedItem(1);
            } else if (destination.getId() == R.id.settingsHome) {
                toolbar.setTitle("Settings");
                if (bottomNav != null) bottomNav.setSelectedItem(2);
            } else if (destination.getId() == R.id.universityDetailsFragment) {
                String universityName = arguments != null ? arguments.getString("UNIVERSITY_NAME", "") : "";
                toolbar.setTitle(universityName);
            } else if (destination.getId() == R.id.studyMaterialFragment) {
                toolbar.setTitle("Study Material");
                if (bottomNav != null) bottomNav.setSelectedItem(1);
            } else {
                toolbar.setTitle(getString(R.string.app_name));
            }
        });
    }

    private void setupBackPressHandling() {
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (Objects.requireNonNull(navController.getCurrentDestination()).getId() == R.id.homeFragment) {
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
        CustomBottomNavigationView bottomNav = findViewById(R.id.custom_bottom_navigation);
        if (bottomNav == null) {
            throw new IllegalStateException("CustomBottomNavigationView not found in activity_main.xml");
        }

        bottomNav.setOnNavigationItemSelectedListener(position -> {
            int currentDestination = Objects.requireNonNull(navController.getCurrentDestination()).getId();
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
                // Apply animation to the clicked navigation item
                View navItem = bottomNav.findViewById(getNavItemId(position));
                if (navItem != null) {
                    applySelectionAnimation(navItem);
                }
                navController.navigate(targetDestination);
            }
        });
    }

    private int getNavItemId(int position) {
        switch (position) {
            case 0:
                return R.id.nav_home;
            case 1:
                return R.id.nav_study;
            case 2:
                return R.id.nav_settings;
            default:
                return -1;
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