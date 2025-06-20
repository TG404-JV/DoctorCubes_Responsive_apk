package com.tvm.doctorcube.settings;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.tvm.doctorcube.R;
import com.tvm.doctorcube.SocialActions;

public class SettingsHome extends Fragment {
    private static final String TAG = "SettingsHome";
    private NavController navController;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private View rootView;

    // Views
    private TextView tvAppVersion;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        authStateListener = firebaseAuth -> {
            FirebaseUser currentUser = firebaseAuth.getCurrentUser();
            if (currentUser == null) {
                Log.e(TAG, "User signed out");
                Toast.makeText(requireContext(), R.string.error_no_user, Toast.LENGTH_SHORT).show();
                navigateToLogin();
            } else {
                Log.d(TAG, "Auth state changed: userId=" + currentUser.getUid());
            }
        };
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_settings_home, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Log.e(TAG, "No authenticated user found");
            Toast.makeText(requireContext(), R.string.error_no_user, Toast.LENGTH_SHORT).show();
            navigateToLogin();
            return;
        }

        // Initialize NavController
        navController = Navigation.findNavController(view);

        // Initialize views
        tvAppVersion = rootView.findViewById(R.id.tv_app_version);

        // Set app version
        setAppVersion();

        // Set up click listeners
        setupClickListeners();
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(authStateListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (authStateListener != null) {
            mAuth.removeAuthStateListener(authStateListener);
        }
    }

    private void setAppVersion() {
        try {
            PackageInfo packageInfo = requireContext().getPackageManager()
                    .getPackageInfo(requireContext().getPackageName(), 0);
            tvAppVersion.setText(getString(R.string.version_format, packageInfo.versionName));
        } catch (PackageManager.NameNotFoundException e) {
            tvAppVersion.setText(R.string.version_unknown);
            Log.e(TAG, "Failed to get app version: ", e);
            Toast.makeText(requireContext(), "Failed to load app version", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupClickListeners() {
        // Account Section
        View layoutEditProfile = rootView.findViewById(R.id.layout_edit_profile);
        View layoutNotificationPreferences = rootView.findViewById(R.id.layout_notification_preferences);
        // Information Section
        View layoutAboutDoctorCubes = rootView.findViewById(R.id.layout_about_doctorcubes);
        View layoutPrivacyPolicy = rootView.findViewById(R.id.layout_privacy_policy);
        View layoutFaq = rootView.findViewById(R.id.layout_faq);
        // Support Section
        View layoutContactSupport = rootView.findViewById(R.id.layout_contact_support);

        if (layoutEditProfile != null) {
            layoutEditProfile.setOnClickListener(v -> safeNavigate(R.id.action_settingsHome_to_fragmentEditDetails));
        }
        if (layoutNotificationPreferences != null) {
            layoutNotificationPreferences.setOnClickListener(v -> safeNavigate(R.id.action_settingsHome_to_notificationPref));
        }
        if (layoutAboutDoctorCubes != null) {
            layoutAboutDoctorCubes.setOnClickListener(v -> safeNavigate(R.id.action_settingsHome_to_fragmentAbout));
        }
        if (layoutPrivacyPolicy != null) {
            layoutPrivacyPolicy.setOnClickListener(v -> safeNavigate(R.id.action_settingsHome_to_fragmentPrivacy));
        }
        if (layoutFaq != null) {
            layoutFaq.setOnClickListener(v -> safeNavigate(R.id.action_settingsHome_to_fragmentFAQ));
        }
        if (layoutContactSupport != null) {
            layoutContactSupport.setOnClickListener(v -> {
                try {
                    SocialActions socialActions = new SocialActions();
                    socialActions.openEmailApp(requireContext());
                } catch (Exception e) {
                    Toast.makeText(requireContext(), R.string.error_no_email_app, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Failed to open email app: ", e);
                }
            });
        }
    }

    private void safeNavigate(int actionId) {
        try {
            if (navController != null && navController.getCurrentDestination() != null) {
                navController.navigate(actionId);
            } else {
                Log.w(TAG, "Navigation not possible: NavController or destination is null");
                Toast.makeText(requireContext(), R.string.error_navigation, Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e(TAG, "Navigation error: ", e);
            Toast.makeText(requireContext(), R.string.error_navigation, Toast.LENGTH_SHORT).show();
        }
    }

    private void navigateToLogin() {
        try {
            if (navController != null && navController.getCurrentDestination() != null) {
              //  navController.navigate(R.id.action_settingsHome_to_loginFragment);
            }
        } catch (Exception e) {
            Log.e(TAG, "Navigation error to login: ", e);
            Toast.makeText(requireContext(), R.string.error_navigation, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        rootView = null;
        tvAppVersion = null;
    }
}