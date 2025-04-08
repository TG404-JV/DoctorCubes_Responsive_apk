package com.tvm.doctorcube.university;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.tvm.doctorcube.R;
import com.tvm.doctorcube.SocialActions;
import com.tvm.doctorcube.authentication.datamanager.EncryptedSharedPreferencesManager;
import com.tvm.doctorcube.dialoguebox.MBBSApplicationDialog;
import com.tvm.doctorcube.university.model.University;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class UniversityDetailsBottomSheet extends BottomSheetDialogFragment {

    private BottomSheetBehavior<View> bottomSheetBehavior;
    private View bottomSheetView;
    private TextView userNameTextView;
    private ImageView universityImageView;
    private TextView universityNameTextView;
    private TextView universityCountryTextView;
    private TextView programDurationTextView;
    private TextView programMediumTextView;
    private MaterialButton applyButton;
    private MaterialButton whatsappButton;
    private EncryptedSharedPreferencesManager sharedPreferencesManager;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());


    public static UniversityDetailsBottomSheet newInstance(int universityimage, String UniversityName) {
        UniversityDetailsBottomSheet fragment = new UniversityDetailsBottomSheet();
        Bundle args = new Bundle();
        args.putInt("universityImage", universityimage);
        args.putString("universityName", UniversityName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        bottomSheetView = inflater.inflate(R.layout.fragment_university_details_bottom_sheet, container, false);
        return bottomSheetView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize views
        userNameTextView = view.findViewById(R.id.user_name);
        universityImageView = view.findViewById(R.id.university_image);
        universityNameTextView = view.findViewById(R.id.university_name);
        universityCountryTextView = view.findViewById(R.id.university_country);
        programDurationTextView = view.findViewById(R.id.program_duration);
        programMediumTextView = view.findViewById(R.id.program_medium);
        applyButton = view.findViewById(R.id.apply_button);
        whatsappButton = view.findViewById(R.id.whatsapp_button);

        View bottomSheet = view.findViewById(R.id.university_details_bottom_sheet);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);

        // Set expanded state AFTER view is laid out
        view.post(() -> {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        });

        // Make sure it's draggable
        bottomSheetBehavior.setDraggable(true);

        // This allows the bottom sheet to expand to full height
        bottomSheetBehavior.setFitToContents(true);

        sharedPreferencesManager = new EncryptedSharedPreferencesManager(requireContext());
        String fullName = sharedPreferencesManager.getString("name", "");
        // Access the arguments properly
        if (getArguments() != null) {
            int universityImage = getArguments().getInt("universityImage", 0);
            String universityName = getArguments().getString("universityName", "");

            // Populate data
            userNameTextView.setText("Dr. " + fullName);
            universityNameTextView.setText(universityName);
            universityImageView.setImageResource(universityImage);
        } else {
            // Handle case when arguments are null
            int universityImage = new University().getBannerResourceId();
            universityImageView.setImageResource(universityImage);
        }

        // Setup RecyclerView

        // Setup buttons
        setupButtons();

        // Apply animations
        animateContent();
    }

    // BottomSheetDialog to confirm when user has already applied
    private void checkAndAddUniversityApplication(String universityName) {
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();
        if (currentUser == null) {
            Toast.makeText(getContext(), "Please login to apply", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = currentUser.getUid();

        // First check shared preferences
        if (sharedPreferencesManager.getBoolean("isApplied", false) && sharedPreferencesManager.getString("universityName", "").equals(universityName)) {
            // User has already applied
            showConfirmationDialog(universityName, userId);
        } else {
            // Check Firestore
            db.collection("app_submissions")
                    .document(userId)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists() && document.contains("universityName")) {
                                showConfirmationDialog(universityName, userId);
                            } else {
                                // No previous application, proceed with new application
                                saveUniversityApplication(universityName, userId);
                            }
                        } else {
                            Log.e("UniversityDetails", "Error checking application status: ", task.getException());
                            Toast.makeText(getContext(), "Error checking application status: " + task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    // Show confirmation dialog when user has already applied
    private void showConfirmationDialog(String universityName, String userId) {
        MBBSApplicationDialog.show(requireContext(), universityName, userId, new MBBSApplicationDialog.OnDialogActionListener() {
            @Override
            public void onProceed(String universityName, String userId) {
                saveUniversityApplication(universityName, userId);
            }

            @Override
            public void onCancel() {
                // Do nothing or log cancellation
            }
        });
    }
    // Save the university application to Firestore
    private void saveUniversityApplication(String universityName, String userId) {
        db = FirebaseFirestore.getInstance();
        Map<String, Object> applicationData = new HashMap<>();
        applicationData.put("universityName", universityName);
        applicationData.put("isApplied", true);
        applicationData.put("appliedDate", new Date());
        // Use SetOptions to merge data.  This will prevent overwriting the entire document.
        db.collection("app_submissions")
                .document(userId)
                .set(applicationData, SetOptions.merge()) // Use merge here
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Application submitted successfully", Toast.LENGTH_SHORT).show();
                    sharedPreferencesManager.putBoolean("isApplied", true);
                    sharedPreferencesManager.putString("universityName", universityName);
                    sharedPreferencesManager.putString("appliedDate", dateFormat.format(new Date())); // Format the date before saving
                    dismiss(); // Close bottom sheet after successful submission
                })
                .addOnFailureListener(e -> {
                    Log.e("UniversityDetails", "Error submitting application: ", e); // Log the error
                    Toast.makeText(getContext(), "Error submitting application: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
    }

    // Add this to your setupButtons() method
    private void setupButtons() {
        applyButton.setOnClickListener(v -> {
            String universityName = universityNameTextView.getText().toString();
            checkAndAddUniversityApplication(universityName);
        });

        whatsappButton.setOnClickListener(v -> {
            // Handle WhatsApp consultation click
            SocialActions socialActions = new SocialActions();
            socialActions.openEmailApp(requireContext());            // Here you would launch your WhatsApp integration
        });
    }


    private void animateContent() {
        // Animate welcome header
        View welcomeHeader = bottomSheetView.findViewById(R.id.welcome_header);
        welcomeHeader.setAlpha(0f);
        welcomeHeader.setTranslationY(50f);
        welcomeHeader.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(500)
                .setStartDelay(100)
                .setInterpolator(new DecelerateInterpolator())
                .start();

        // Animate university card
        View universityCard = bottomSheetView.findViewById(R.id.university_card);
        universityCard.setAlpha(0f);
        universityCard.setTranslationY(50f);
        universityCard.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(500)
                .setStartDelay(200)
                .setInterpolator(new DecelerateInterpolator())
                .start();

        // Animate application message
        TextView applicationMessage = bottomSheetView.findViewById(R.id.application_message);
        applicationMessage.setAlpha(0f);
        applicationMessage.setTranslationY(50f);
        applicationMessage.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(500)
                .setStartDelay(300)
                .setInterpolator(new DecelerateInterpolator())
                .start();

        // Animate buttons with bounce effect
        AnimatorSet buttonAnimSet = new AnimatorSet();
        ObjectAnimator scaleXAnim = ObjectAnimator.ofFloat(applyButton, "scaleX", 0.8f, 1.1f, 1f);
        ObjectAnimator scaleYAnim = ObjectAnimator.ofFloat(applyButton, "scaleY", 0.8f, 1.1f, 1f);
        ObjectAnimator alphaAnim = ObjectAnimator.ofFloat(applyButton, "alpha", 0f, 1f);

        buttonAnimSet.playTogether(scaleXAnim, scaleYAnim, alphaAnim);
        buttonAnimSet.setDuration(500);
        buttonAnimSet.setStartDelay(400);
        buttonAnimSet.start();

        // Animate WhatsApp button
        whatsappButton.setAlpha(0f);
        whatsappButton.setScaleX(0.8f);
        whatsappButton.setScaleY(0.8f);
        whatsappButton.animate()
                .alpha(1f)
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(500)
                .setStartDelay(450)
                .start();
    }
}

