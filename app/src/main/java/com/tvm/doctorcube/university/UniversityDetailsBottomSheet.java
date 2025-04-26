package com.tvm.doctorcube.university;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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
import java.util.Objects;

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
    private ProgressBar progressBar;
    private EncryptedSharedPreferencesManager sharedPreferencesManager;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

    public static UniversityDetailsBottomSheet newInstance(int universityImage, String universityName, String country) {
        UniversityDetailsBottomSheet fragment = new UniversityDetailsBottomSheet();
        Bundle args = new Bundle();
        args.putInt("universityImage", universityImage);
        args.putString("universityName", universityName);
        args.putString("country", country);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        sharedPreferencesManager = new EncryptedSharedPreferencesManager(requireContext());
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
        progressBar = view.findViewById(R.id.progress_bar);

        // Setup bottom sheet behavior
        View bottomSheet = view.findViewById(R.id.university_details_bottom_sheet);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        bottomSheetBehavior.setDraggable(true);
        bottomSheetBehavior.setFitToContents(true);

        // Close button

        // Populate data
        String fullName = sharedPreferencesManager.getString("name", "User");
        Bundle args = getArguments();
        if (args != null) {
            int universityImage = args.getInt("universityImage", R.drawable.icon_university);
            String universityName = args.getString("universityName", "Unknown University");
            String country = args.getString("country", "Unknown");

            userNameTextView.setText("Dr. " + fullName);
            universityNameTextView.setText(universityName);
            universityImageView.setImageResource(universityImage);
            universityCountryTextView.setText(country);
            programDurationTextView.setText("6 Years"); // Replace with dynamic data if available
            programMediumTextView.setText("English"); // Replace with dynamic data if available
        } else {
            Log.w("UniversityDetails", "Arguments are null, using defaults");
            userNameTextView.setText("Dr. " + fullName);
            universityNameTextView.setText("Unknown University");
            universityImageView.setImageResource(new University().getBannerResourceId());
            universityCountryTextView.setText("Unknown");
            programDurationTextView.setText("N/A");
            programMediumTextView.setText("N/A");
        }

        // Setup buttons
        setupButtons();

        // Apply animations
        animateContent();
    }

    private void checkAndAddUniversityApplication(String universityName) {
        if (currentUser == null) {
            Toast.makeText(getContext(), "Please log in to apply", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = currentUser.getUid();
        showProgress(true);

        // Check shared preferences
        if (sharedPreferencesManager.getBoolean("isApplied", false) &&
                sharedPreferencesManager.getString("universityName", "").equals(universityName)) {
            showProgress(false);
            showConfirmationDialog(universityName, userId);
        } else {
            // Check Firestore
            db.collection("app_submissions")
                    .document(userId)
                    .get()
                    .addOnCompleteListener(task -> {
                        showProgress(false);
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists() && document.contains("universityName")) {
                                showConfirmationDialog(universityName, userId);
                            } else {
                                saveUniversityApplication(universityName, userId);
                            }
                        } else {
                            Log.e("UniversityDetails", "Error checking application: ", task.getException());
                            Toast.makeText(getContext(), "Error: " + Objects.requireNonNull(task.getException()).getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void showConfirmationDialog(String universityName, String userId) {
        MBBSApplicationDialog.show(requireContext(), universityName, userId, new MBBSApplicationDialog.OnDialogActionListener() {
            @Override
            public void onProceed(String universityName, String userId) {
                saveUniversityApplication(universityName, userId);
            }

            @Override
            public void onCancel() {
                // No action needed
            }
        });
    }

    private void saveUniversityApplication(String universityName, String userId) {
        showProgress(true);
        Map<String, Object> applicationData = new HashMap<>();
        applicationData.put("universityName", universityName);
        applicationData.put("isApplied", true);
        applicationData.put("appliedDate", new Date());

        db.collection("app_submissions")
                .document(userId)
                .set(applicationData, SetOptions.merge())
                .addOnSuccessListener(aVoid -> {
                    showProgress(false);
                    Toast.makeText(getContext(), "Application submitted successfully", Toast.LENGTH_SHORT).show();
                    sharedPreferencesManager.putBoolean("isApplied", true);
                    sharedPreferencesManager.putString("universityName", universityName);
                    sharedPreferencesManager.putString("appliedDate", dateFormat.format(new Date()));
                    dismiss();
                })
                .addOnFailureListener(e -> {
                    showProgress(false);
                    Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void setupButtons() {
        applyButton.setOnClickListener(v -> {
            String universityName = universityNameTextView.getText().toString();
            if (!universityName.isEmpty()) {
                checkAndAddUniversityApplication(universityName);
            } else {
                Toast.makeText(getContext(), "University name is missing", Toast.LENGTH_SHORT).show();
            }
        });

        whatsappButton.setOnClickListener(v -> {
            SocialActions socialActions = new SocialActions();
            socialActions.openWhatsApp(requireContext());
        });
    }

    private void animateContent() {
        // Animate welcome header
        View welcomeHeader = bottomSheetView.findViewById(R.id.welcome_header);
        animateView(welcomeHeader, 100);

        // Animate university card
        View universityCard = bottomSheetView.findViewById(R.id.university_card);
        animateView(universityCard, 200);

        // Animate application message
        TextView applicationMessage = bottomSheetView.findViewById(R.id.application_message);
        animateView(applicationMessage, 300);

        // Animate buttons
        AnimatorSet buttonAnimSet = new AnimatorSet();
        ObjectAnimator scaleXAnim = ObjectAnimator.ofFloat(applyButton, "scaleX", 0.8f, 1f);
        ObjectAnimator scaleYAnim = ObjectAnimator.ofFloat(applyButton, "scaleY", 0.8f, 1f);
        ObjectAnimator alphaAnim = ObjectAnimator.ofFloat(applyButton, "alpha", 0f, 1f);
        buttonAnimSet.playTogether(scaleXAnim, scaleYAnim, alphaAnim);
        buttonAnimSet.setDuration(300);
        buttonAnimSet.setStartDelay(400);
        buttonAnimSet.start();

        animateView(whatsappButton, 450);
    }

    private void animateView(View view, long startDelay) {
        if (view != null) {
            view.setAlpha(0f);
            view.setTranslationY(50f);
            view.animate()
                    .alpha(1f)
                    .translationY(0f)
                    .setDuration(300)
                    .setStartDelay(startDelay)
                    .setInterpolator(new DecelerateInterpolator())
                    .start();
        }
    }

    private void showProgress(boolean show) {
        if (progressBar != null) {
            progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
            applyButton.setEnabled(!show);
            whatsappButton.setEnabled(!show);
        }
    }
}