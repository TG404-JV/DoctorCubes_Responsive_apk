package com.tvm.doctorcube.settings;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.tvm.doctorcube.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class FragmentEditDetails extends Fragment {

    private CircleImageView profileImageView;
    private ImageView editProfileImageBtn;
    private TextView userNameDisplay, applicationStatusText, neetScoreDisplay, universityDisplay, studyCountryDisplay;
    private TextView appliedDateText, lastUpdatedText, lastCallDateText, universityNameText;
    private TextInputEditText nameEditText, emailEditText, mobileEditText, countryEditText, stateEditText, cityEditText;
    private TextInputEditText neetScoreEditText, studyCountryEditText;
    private CheckBox hasNeetScoreCheckbox, hasPassportCheckbox;
    private Button editModeToggleBtn, cancelBtn, saveBtn, callBtn, whatsappBtn, viewUniversityDetailsBtn;
    private View actionButtonsLayout;

    private FirebaseAuth mAuth;
    private DocumentReference userRef;
    private StorageReference storageRef;
    private boolean isEditMode = false;
    private Uri imageUri;
    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yy", Locale.getDefault());

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_details, container, false);
        initializeViews(view);
        initializeFirebase();
        setupImagePicker();
        loadUserData();
        setupClickListeners();
        return view;
    }

    private void initializeViews(View view) {
        profileImageView = view.findViewById(R.id.profileImageView);
        editProfileImageBtn = view.findViewById(R.id.editProfileImageBtn);
        userNameDisplay = view.findViewById(R.id.userNameDisplay);
        applicationStatusText = view.findViewById(R.id.applicationStatusText);
        neetScoreDisplay = view.findViewById(R.id.neetScoreDisplay);
        universityDisplay = view.findViewById(R.id.universityDisplay);
        studyCountryDisplay = view.findViewById(R.id.studyCountryDisplay);
        appliedDateText = view.findViewById(R.id.appliedDateText);
        lastUpdatedText = view.findViewById(R.id.lastUpdatedText);
        lastCallDateText = view.findViewById(R.id.lastCallDateText);
        universityNameText = view.findViewById(R.id.universityNameText);
        nameEditText = view.findViewById(R.id.nameEditText);
        emailEditText = view.findViewById(R.id.emailEditText);
        mobileEditText = view.findViewById(R.id.mobileEditText);
        countryEditText = view.findViewById(R.id.countryEditText);
        stateEditText = view.findViewById(R.id.stateEditText);
        cityEditText = view.findViewById(R.id.cityEditText);
        neetScoreEditText = view.findViewById(R.id.neetScoreEditText);
        studyCountryEditText = view.findViewById(R.id.studyCountryEditText);
        hasNeetScoreCheckbox = view.findViewById(R.id.hasNeetScoreCheckbox);
        hasPassportCheckbox = view.findViewById(R.id.hasPassportCheckbox);
        editModeToggleBtn = view.findViewById(R.id.editModeToggleBtn);
        cancelBtn = view.findViewById(R.id.cancelBtn);
        saveBtn = view.findViewById(R.id.saveBtn);
        callBtn = view.findViewById(R.id.callBtn);
        whatsappBtn = view.findViewById(R.id.whatsappBtn);
        viewUniversityDetailsBtn = view.findViewById(R.id.viewUniversityDetailsBtn);
        actionButtonsLayout = view.findViewById(R.id.actionButtonsLayout);
    }

    private void initializeFirebase() {
        mAuth = FirebaseAuth.getInstance();
        String userId = mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getUid() : "";
        if (userId.isEmpty()) {
            Toast.makeText(getContext(), "User not authenticated", Toast.LENGTH_SHORT).show();
            setDefaultUI();
            return;
        }
        userRef = FirebaseFirestore.getInstance().collection("app_submissions").document(userId);
        storageRef = FirebaseStorage.getInstance().getReference("profile_images").child(userId + "/profile.jpg");
    }

    private void setupImagePicker() {
        imagePickerLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        imageUri = result.getData().getData();
                        Glide.with(this).load(imageUri).into(profileImageView);
                    }
                });
    }

    private void loadUserData() {
        if (userRef == null) {
            setDefaultUI();
            return;
        }

        userRef.get().addOnSuccessListener(documentSnapshot -> {
            if (!isAdded()) return;

            if (documentSnapshot.exists()) {
                // Set default values if fields don't exist
                String name = documentSnapshot.contains("name") ? documentSnapshot.getString("name") : "Unknown User";
                String email = documentSnapshot.contains("email") ? documentSnapshot.getString("name") : "No Email";
                String mobile = documentSnapshot.contains("mobile") ? documentSnapshot.getString("mobile") : "No Mobile";
                String country = documentSnapshot.contains("country") ? documentSnapshot.getString("country") : "Unknown";
                String state = documentSnapshot.contains("state") ? documentSnapshot.getString("state") : "Unknown";
                String city = documentSnapshot.contains("city") ? documentSnapshot.getString("city") : "Unknown";
                String neetScore = documentSnapshot.contains("neetScore") ? documentSnapshot.getString("neetScore") : "0";
                String studyCountry = documentSnapshot.contains("studyCountry") ? documentSnapshot.getString("studyCountry") : "Unknown";
                String universityName = documentSnapshot.contains("universityName") ? documentSnapshot.getString("universityName") : "Unknown University";
                String lastCallDate = documentSnapshot.contains("lastCallDate") ? documentSnapshot.getString("lastCallDate") : "Not Called Yet";
                Boolean hasNeetScore = documentSnapshot.contains("hasNeetScore") ? documentSnapshot.getBoolean("hasNeetScore") : false;
                Boolean hasPassport = documentSnapshot.contains("hasPassport") ? documentSnapshot.getBoolean("hasPassport") : false;
                Boolean isAdmitted = documentSnapshot.contains("isAdmitted") ? documentSnapshot.getBoolean("isAdmitted") : false;
                Boolean isApplied = documentSnapshot.contains("isApplied") ? documentSnapshot.getBoolean("isApplied") : false;

                // Handle dates
                String appliedDate = documentSnapshot.contains("appliedDate") ?
                        formatTimestamp(documentSnapshot.getTimestamp("appliedDate")) : "N/A";
                String lastUpdated = documentSnapshot.contains("lastUpdatedDate") ?
                        formatStringDate(documentSnapshot.getString("lastUpdatedDate")) : dateFormat.format(new Date());

                // Determine application status
                String status = isAdmitted ? "Admitted" : isApplied ? "Application Submitted" : lastCallDate.equals("Not Called Yet") ? "Under Review" : "Applied";

                // Update UI
                userNameDisplay.setText(name);
                applicationStatusText.setText(status);
                neetScoreDisplay.setText(neetScore);
                universityDisplay.setText(universityName);
                studyCountryDisplay.setText(studyCountry);
                appliedDateText.setText(appliedDate);
                lastUpdatedText.setText(lastUpdated);
                lastCallDateText.setText(lastCallDate);
                universityNameText.setText(universityName);
                nameEditText.setText(name);
                emailEditText.setText(email);
                mobileEditText.setText(mobile);
                countryEditText.setText(country);
                stateEditText.setText(state);
                cityEditText.setText(city);
                neetScoreEditText.setText(neetScore);
                studyCountryEditText.setText(studyCountry);
                hasNeetScoreCheckbox.setChecked(hasNeetScore);
                hasPassportCheckbox.setChecked(hasPassport);

                // Update status badge color
                int statusColor = isAdmitted ? R.color.medical_accent_green :
                        status.equals("Under Review") ? R.color.status_warning : R.color.medical_accent_green;
                applicationStatusText.getRootView().setBackgroundTintList(getResources().getColorStateList(statusColor, getContext().getTheme()));

                // Load profile image
                loadProfileImage();
            } else {
                Toast.makeText(getContext(), "No user data found", Toast.LENGTH_SHORT).show();
                setDefaultUI();
            }
        }).addOnFailureListener(e -> {
            if (!isAdded()) return;
            if (e instanceof FirebaseFirestoreException && ((FirebaseFirestoreException) e).getCode() == FirebaseFirestoreException.Code.PERMISSION_DENIED) {
                Toast.makeText(getContext(), "Permission denied. Please check your authentication status.", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getContext(), "Failed to load data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
            setDefaultUI();
        });
    }

    private void setDefaultUI() {
        String defaultValue = "N/A";
        userNameDisplay.setText("Unknown User");
        applicationStatusText.setText("Under Review");
        neetScoreDisplay.setText("0");
        universityDisplay.setText("Unknown University");
        studyCountryDisplay.setText("Unknown");
        appliedDateText.setText(defaultValue);
        lastUpdatedText.setText(dateFormat.format(new Date()));
        lastCallDateText.setText("Not Called Yet");
        universityNameText.setText("Unknown University");
        nameEditText.setText("Unknown User");
        emailEditText.setText("No Email");
        mobileEditText.setText("No Mobile");
        countryEditText.setText("Unknown");
        stateEditText.setText("Unknown");
        cityEditText.setText("Unknown");
        neetScoreEditText.setText("0");
        studyCountryEditText.setText("Unknown");
        hasNeetScoreCheckbox.setChecked(false);
        hasPassportCheckbox.setChecked(false);
        applicationStatusText.getRootView().setBackgroundTintList(getResources().getColorStateList(R.color.status_warning, getContext().getTheme()));
        loadProfileImage();
    }

    private String formatTimestamp(com.google.firebase.Timestamp timestamp) {
        if (timestamp == null) return "N/A";
        try {
            Date date = timestamp.toDate();
            return dateFormat.format(date);
        } catch (Exception e) {
            return "N/A";
        }
    }

    private String formatStringDate(String dateStr) {
        if (dateStr == null || dateStr.isEmpty()) return "N/A";
        try {
            Date date = dateFormat.parse(dateStr);
            return dateFormat.format(date);
        } catch (ParseException e) {
            return "N/A";
        }
    }

    private void loadProfileImage() {
        storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
            if (isAdded()) {
                Glide.with(FragmentEditDetails.this).load(uri).placeholder(R.drawable.ic_profile).into(profileImageView);
            }
        }).addOnFailureListener(e -> {
            if (isAdded()) {
                profileImageView.setImageResource(R.drawable.ic_profile);
            }
        });
    }

    private void setupClickListeners() {
        editModeToggleBtn.setOnClickListener(v -> toggleEditMode());
        cancelBtn.setOnClickListener(v -> toggleEditMode());
        saveBtn.setOnClickListener(v -> saveChanges());
        editProfileImageBtn.setOnClickListener(v -> pickImage());
        callBtn.setOnClickListener(v -> initiateCall());
        whatsappBtn.setOnClickListener(v -> initiateWhatsApp());
        viewUniversityDetailsBtn.setOnClickListener(v -> viewUniversityDetails());
    }

    private void toggleEditMode() {
        isEditMode = !isEditMode;
        editModeToggleBtn.setText(isEditMode ? "Done" : "Edit");
        actionButtonsLayout.setVisibility(isEditMode ? View.VISIBLE : View.GONE);
        editProfileImageBtn.setVisibility(isEditMode ? View.VISIBLE : View.GONE);
        setFieldsEditable(isEditMode);
    }

    private void setFieldsEditable(boolean editable) {
        nameEditText.setEnabled(editable);
        emailEditText.setEnabled(editable);
        mobileEditText.setEnabled(editable);
        countryEditText.setEnabled(editable);
        stateEditText.setEnabled(editable);
        cityEditText.setEnabled(editable);
        neetScoreEditText.setEnabled(editable);
        studyCountryEditText.setEnabled(editable);
        hasNeetScoreCheckbox.setEnabled(editable);
        hasPassportCheckbox.setEnabled(editable);
    }

    private void pickImage() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        imagePickerLauncher.launch(intent);
    }

    private void saveChanges() {
        if (userRef == null) {
            Toast.makeText(getContext(), "Cannot save: User not authenticated", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> updates = new HashMap<>();
        updates.put("name", nameEditText.getText().toString().trim());
        updates.put("email", emailEditText.getText().toString().trim());
        updates.put("mobile", mobileEditText.getText().toString().trim());
        updates.put("country", countryEditText.getText().toString().trim());
        updates.put("state", stateEditText.getText().toString().trim());
        updates.put("city", cityEditText.getText().toString().trim());
        updates.put("neetScore", neetScoreEditText.getText().toString().trim());
        updates.put("studyCountry", studyCountryEditText.getText().toString().trim());
        updates.put("hasNeetScore", hasNeetScoreCheckbox.isChecked());
        updates.put("hasPassport", hasPassportCheckbox.isChecked());
        updates.put("lastUpdatedDate", dateFormat.format(new Date()));

        userRef.set(updates).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(getContext(), "Profile updated successfully", Toast.LENGTH_SHORT).show();
                toggleEditMode();
            } else {
                String errorMsg = task.getException() != null ? task.getException().getMessage() : "Unknown error";
                if (task.getException() instanceof FirebaseFirestoreException &&
                        ((FirebaseFirestoreException) task.getException()).getCode() == FirebaseFirestoreException.Code.PERMISSION_DENIED) {
                    Toast.makeText(getContext(), "Permission denied. Please check your authentication status.", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getContext(), "Failed to update profile: " + errorMsg, Toast.LENGTH_SHORT).show();
                }
            }
        });

        if (imageUri != null) {
            storageRef.putFile(imageUri).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    loadProfileImage();
                } else {
                    String errorMsg = task.getException() != null ? task.getException().getMessage() : "Unknown error";
                    Toast.makeText(getContext(), "Failed to upload image: " + errorMsg, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void initiateCall() {
        Intent callIntent = new Intent(Intent.ACTION_DIAL);
        callIntent.setData(Uri.parse("tel:+1234567890")); // Replace with actual number
        startActivity(callIntent);

        if (userRef != null) {
            userRef.update("lastCallDate", dateFormat.format(new Date())).addOnFailureListener(e -> {
                Toast.makeText(getContext(), "Failed to update call date: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });
        }
    }

    private void initiateWhatsApp() {
        String phoneNumber = "+1234567890"; // Replace with actual number
        String url = "https://wa.me/" + phoneNumber;
        Intent whatsappIntent = new Intent(Intent.ACTION_VIEW);
        whatsappIntent.setData(Uri.parse(url));
        startActivity(whatsappIntent);
    }

    private void viewUniversityDetails() {
        Toast.makeText(getContext(), "Navigating to university details", Toast.LENGTH_SHORT).show();
        // Implement navigation to university details screen
    }
}