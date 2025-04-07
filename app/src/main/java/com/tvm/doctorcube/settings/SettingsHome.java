package com.tvm.doctorcube.settings;



import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.tvm.doctorcube.R;
import com.tvm.doctorcube.SocialActions;
import com.tvm.doctorcube.authentication.datamanager.EncryptedSharedPreferencesManager;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.File;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;


public class SettingsHome extends Fragment {
    private static final String PREFS_NAME = "UserProfilePrefs";
    private NavController navController;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private SharedPreferences sharedPreferences;
    private String userId;
    private FirebaseFirestore mFirestore;

    // Views
    private TextView userName, userEmail, userStatus;
    private ImageView profileImage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize Firebase and SharedPreferences
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            userId = currentUser.getUid();
        }
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        sharedPreferences = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        mFirestore = FirebaseFirestore.getInstance(); // Initialize Firestore

        // Initialize NavController
        navController = Navigation.findNavController(view);

        TextView appVersionTextView = view.findViewById(R.id.appVersion);
        // Get App Version
        try {
            PackageManager packageManager = requireContext().getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(requireContext().getPackageName(), 0);
            String versionName = packageInfo.versionName;

            // Set version to TextView
            appVersionTextView.setText("Version: " + versionName);
        } catch (PackageManager.NameNotFoundException e) {
            appVersionTextView.setText("Version: Unknown");
        }

        view.findViewById(R.id.schedule_consultation_button).setOnClickListener(v -> {
            SocialActions openSocialActions = new SocialActions();
            openSocialActions.openEmailApp(requireContext());
        });


        // Initialize views
        initViews(view);

        // Load student data
        loadStudentData();

        // Set up click listeners
        setupClickListeners();

        // Handle toolbar
        setupToolbar(view);
    }

    private void initViews(View view) {
        // Profile views
        profileImage = view.findViewById(R.id.profile_image);
        userName = view.findViewById(R.id.user_name);
        userEmail = view.findViewById(R.id.user_email);
        userStatus = view.findViewById(R.id.user_status);
    }

    private void setupClickListeners() {
        // Find views
        LinearLayout profileCard = requireView().findViewById(R.id.profile_settings_layout);
        LinearLayout aboutLayout = requireView().findViewById(R.id.about_layout);
        LinearLayout privacyLayout = requireView().findViewById(R.id.privacy_policy_layout);
        LinearLayout faqLayout = requireView().findViewById(R.id.faq_layout);
        LinearLayout notificationLayout = requireView().findViewById(R.id.notification_settings_layout);

        Button consultationButton = requireView().findViewById(R.id.schedule_consultation_button);

        // Set click listeners with safe navigation
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

    private void setupToolbar(View view) {
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setNavigationOnClickListener(v -> {
                if (navController.getCurrentDestination() != null &&
                        navController.getCurrentDestination().getId() != R.id.settingsHome) {
                    navController.popBackStack(R.id.settingsHome, false);
                } else {
                    requireActivity().getOnBackPressedDispatcher();
                }
            });
        }
    }

    private void loadStudentData() {
        // Try loading from SharedPreferences first
        EncryptedSharedPreferencesManager encryptedSharedPreferencesManager = new EncryptedSharedPreferencesManager(requireActivity());
        String name = encryptedSharedPreferencesManager.getString("name", "");
        String email = encryptedSharedPreferencesManager.getString("email","");
        String status = "Becoming A Doctor";

        if (!name.isEmpty() && !email.isEmpty()) {
            updateUI(name, email, status);
            loadProfileImage();
        } else {
            fetchDataFromFirebase();
        }
    }

    private void fetchDataFromFirebase() {
        if (userId != null) {
            mFirestore.collection("Users").document(userId) // Use Firestore
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String name = documentSnapshot.getString("fullName");
                            String email = documentSnapshot.getString("email");
                            String status = documentSnapshot.getString("status");
                            String imageUrl = documentSnapshot.getString("imageUrl"); // Get image URL

                            // Use defaults if null
                            name = name != null ? name : "Unknown User";
                            email = email != null ? email : "No email";
                            status = status != null ? status : "Student • USA Aspirant";



                            updateUI(name, email, status);
                            loadProfileImage(); // Load image
                        }
                    })
                    .addOnFailureListener(e -> {
                        fetchDataFromRealtimeDatabase(); // Fallback to Realtime Database
                    });
        }
    }

    private void fetchDataFromRealtimeDatabase() {
        if (userId != null) {
            mDatabase.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String name = snapshot.child("name").getValue(String.class);
                        String email = snapshot.child("email").getValue(String.class);
                        String status = snapshot.child("status").getValue(String.class);

                        // Use defaults if null
                        name = name != null ? name : "Unknown User";
                        email = email != null ? email : "No email";
                        status = status != null ? status : "Student • USA Aspirant";

                        // Save to SharedPreferences
                       EncryptedSharedPreferencesManager encryptedSharedPreferencesManager = new EncryptedSharedPreferencesManager(requireContext());

                        updateUI(name, email, status);
                        loadProfileImage(); // Load image
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    updateUI("John Smith", "john.smith@example.com", "Student • USA Aspirant"); // Fallback
                }
            });
        }
    }

    private void updateUI(String name, String email, String status) {
        userName.setText(name);
        userEmail.setText(email);
        userStatus.setText(status);
    }

    private void loadProfileImage() {
            loadLocalProfileImage();

    }


    private void loadLocalProfileImage() {
        EncryptedSharedPreferencesManager encryptedSharedPreferencesManager = new EncryptedSharedPreferencesManager(requireContext());
        File img = new File(encryptedSharedPreferencesManager.getString("FilePath", ""));
        Glide.with(this).load(img).circleCrop().into(profileImage);
    }



    private void safeNavigate(int actionId) {
        try {
            if (navController != null && navController.getCurrentDestination() != null) {
                navController.navigate(actionId);
            } else {
            }
        } catch (Exception ignored) {
        }
    }
}

