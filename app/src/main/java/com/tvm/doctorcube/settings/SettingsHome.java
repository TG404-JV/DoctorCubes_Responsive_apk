package com.tvm.doctorcube.settings;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.tvm.doctorcube.R;
import com.tvm.doctorcube.SocialActions;
import com.tvm.doctorcube.authentication.datamanager.EncryptedSharedPreferencesManager;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class SettingsHome extends Fragment {
    private static final String PREFS_NAME = "UserProfilePrefs";
    private static final String TAG = "SettingsHome";
    private NavController navController;
    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;
    private FirebaseAuth.AuthStateListener authStateListener;
    private String userId;

    // Views
    private TextView userName, userEmail, userStatus;
    private ImageView profileImage;

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
                userId = currentUser.getUid();
                Log.d(TAG, "Auth state changed: userId=" + userId);
            }
        };
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            userId = currentUser.getUid();
            Log.d(TAG, "User ID initialized: " + userId + ", Email: " + currentUser.getEmail());
        } else {
            Log.e(TAG, "No authenticated user found");
            Toast.makeText(requireContext(), R.string.error_no_user, Toast.LENGTH_SHORT).show();
            navigateToLogin();
            return;
        }

        // Initialize NavController
        navController = Navigation.findNavController(view);

        // Set app version
        TextView appVersionTextView = view.findViewById(R.id.appVersion);
        try {
            PackageInfo packageInfo = requireContext().getPackageManager()
                    .getPackageInfo(requireContext().getPackageName(), 0);
            appVersionTextView.setText(getString(R.string.version_format, packageInfo.versionName));
        } catch (PackageManager.NameNotFoundException e) {
            appVersionTextView.setText(R.string.version_unknown);
        }

        // Set consultation button listener
        view.findViewById(R.id.schedule_consultation_button).setOnClickListener(v -> {
            try {
                SocialActions openSocialActions = new SocialActions();
                openSocialActions.openEmailApp(requireContext());
            } catch (Exception e) {
                Toast.makeText(requireContext(), R.string.error_no_email_app, Toast.LENGTH_SHORT).show();
            }
        });

        // Initialize views
        initViews(view);

        // Load user data
        loadStudentData();

        // Set up click listeners
        setupClickListeners();

        // Handle toolbar
        setupToolbar();
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

    private void initViews(View view) {
        profileImage = view.findViewById(R.id.profile_image);
        userName = view.findViewById(R.id.user_name);
        userEmail = view.findViewById(R.id.user_email);
        userStatus = view.findViewById(R.id.user_status);
    }

    private void setupClickListeners() {
        MaterialCardView profileCard = requireView().findViewById(R.id.profile_settings_layout);
        MaterialCardView aboutLayout = requireView().findViewById(R.id.about_layout);
        MaterialCardView privacyLayout = requireView().findViewById(R.id.privacy_policy_layout);
        MaterialCardView faqLayout = requireView().findViewById(R.id.faq_layout);
        MaterialCardView notificationLayout = requireView().findViewById(R.id.notification_settings_layout);

        if (profileCard != null) {
            profileCard.setOnClickListener(v -> safeNavigate(R.id.action_settingsHome_to_fragmentEditDetails));
        }
        if (aboutLayout != null) {
            aboutLayout.setOnClickListener(v -> safeNavigate(R.id.action_settingsHome_to_fragmentAbout));
        }
        if (privacyLayout != null) {
            privacyLayout.setOnClickListener(v -> safeNavigate(R.id.action_settingsHome_to_fragmentPrivacy));
        }
        if (faqLayout != null) {
            faqLayout.setOnClickListener(v -> safeNavigate(R.id.action_settingsHome_to_fragmentFAQ));
        }
        if (notificationLayout != null) {
            notificationLayout.setOnClickListener(v -> safeNavigate(R.id.action_settingsHome_to_notificationPref));
        }
    }

    private void setupToolbar() {
        if (getActivity() instanceof AppCompatActivity activity) {
            Toolbar toolbar = activity.findViewById(R.id.toolbar);
            if (toolbar != null) {
                TextView appTitle = toolbar.findViewById(R.id.app_title);
                if (appTitle != null) {
                    appTitle.setText(R.string.settings_title);
                }
            }
        }
    }

    private void loadStudentData() {
        EncryptedSharedPreferencesManager prefs = new EncryptedSharedPreferencesManager(requireContext());
        String name = prefs.getString("name", null);
        String email = prefs.getString("email", null);
        String status = prefs.getString("status", getString(R.string.default_status));

        if (name != null && email != null) {
            updateUI(name, email, status);
            loadProfileImage();
        } else {
            fetchDataFromFirestore();
        }
    }

    private void fetchDataFromFirestore() {
        if (userId == null) {
            updateUI(getString(R.string.default_name), getString(R.string.default_email), getString(R.string.default_status));
            navigateToLogin();
            return;
        }

        mFirestore.collection("Users").document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String name = documentSnapshot.getString("name");
                        String email = documentSnapshot.getString("email");
                        String status = documentSnapshot.getString("status");
                        String imageUrl = documentSnapshot.getString("imageUrl");
                        String phone = documentSnapshot.getString("mobile");

                        name = name != null ? name : getString(R.string.default_name);
                        email = email != null ? email : getString(R.string.default_email);
                        status = status != null ? status : getString(R.string.default_status);
                        phone = phone != null ? phone : getString(R.string.sample_phone);

                        EncryptedSharedPreferencesManager prefs = new EncryptedSharedPreferencesManager(requireContext());
                        prefs.putString("name", name);
                        prefs.putString("email", email);
                        prefs.putString("status", status);
                        prefs.putString("phone", phone);
                        if (imageUrl != null) {
                            prefs.putString("imageUrl", imageUrl);
                        }

                        updateUI(name, email, status);
                        loadProfileImage();
                    } else {
                        updateUI(getString(R.string.default_name), getString(R.string.default_email), getString(R.string.default_status));
                        Toast.makeText(requireContext(), R.string.error_loading, Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Firestore error: ", e);
                    updateUI(getString(R.string.default_name), getString(R.string.default_email), getString(R.string.default_status));
                    Toast.makeText(requireContext(), "Failed to load profile", Toast.LENGTH_SHORT).show();
                });
    }

    private void updateUI(String name, String email, String status) {
        userName.setText(name);
        userEmail.setText(email);
        userStatus.setText(status);
    }

    private void loadProfileImage() {
        EncryptedSharedPreferencesManager prefs = new EncryptedSharedPreferencesManager(requireContext());
        String localFilePath = prefs.getString("FilePath", null);
        String imageUrl = prefs.getString("imageUrl", null);

        if (localFilePath != null && !localFilePath.isEmpty()) {
            File imgFile = new File(localFilePath);
            if (imgFile.exists()) {
                Glide.with(this)
                        .load(imgFile)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true)
                        .circleCrop()
                        .placeholder(R.drawable.logo_doctor_cubes_white)
                        .error(R.drawable.logo_doctor_cubes_white)
                        .into(profileImage);
                return;
            }
        }

        if (imageUrl != null && !imageUrl.isEmpty()) {
            try {
                Glide.with(this)
                        .load(imageUrl)
                        .diskCacheStrategy(DiskCacheStrategy.DATA)
                        .circleCrop()
                        .placeholder(R.drawable.logo_doctor_cubes_white)
                        .error(R.drawable.logo_doctor_cubes_white)
                        .into(profileImage);
                saveImageToLocalStorage(imageUrl);
            } catch (Exception e) {
                Log.e(TAG, "Error loading image from Firebase Storage: ", e);
                profileImage.setImageResource(R.drawable.logo_doctor_cubes_white);
                Toast.makeText(requireContext(), "Failed to load profile image", Toast.LENGTH_SHORT).show();
            }
        } else {
            profileImage.setImageResource(R.drawable.logo_doctor_cubes_white);
        }
    }

    private void saveImageToLocalStorage(String imageUrl) {
        Glide.with(this)
                .asBitmap()
                .load(imageUrl)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(new com.bumptech.glide.request.target.CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable com.bumptech.glide.request.transition.Transition<? super Bitmap> transition) {
                        try {
                            File outputDir = requireContext().getFilesDir();
                            File outputFile = new File(outputDir, userId + "_profile.jpg");
                            try (FileOutputStream fos = new FileOutputStream(outputFile)) {
                                resource.compress(Bitmap.CompressFormat.JPEG, 80, fos);
                                EncryptedSharedPreferencesManager prefs = new EncryptedSharedPreferencesManager(requireContext());
                                prefs.putString("FilePath", outputFile.getAbsolutePath());
                            }
                        } catch (IOException e) {
                            Log.e(TAG, "Error saving image to local storage: ", e);
                        }
                    }

                    @Override
                    public void onLoadCleared(@Nullable android.graphics.drawable.Drawable placeholder) {}
                });
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
               // navController.navigate(R.id.action_settingsHome_to_loginFragment);
            }
        } catch (Exception e) {
            Log.e(TAG, "Navigation error: ", e);
            Toast.makeText(requireContext(), R.string.error_navigation, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        profileImage = null;
        userName = null;
        userEmail = null;
        userStatus = null;
    }
}