package com.tvm.doctorcube.settings;

import android.animation.Animator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.tvm.doctorcube.CustomToast;
import com.tvm.doctorcube.R;
import com.tvm.doctorcube.authentication.datamanager.EncryptedSharedPreferencesManager;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.airbnb.lottie.LottieAnimationView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class FragmentEditDetails extends Fragment {

    private static final int GALLERY_REQUEST_CODE = 1001;
    private static final String PREFS_NAME = "UserProfilePrefs";
    private static final String TAG = "EditDetailsFragment";

    // Firebase
    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;
    private FirebaseStorage mStorage;
    private FirebaseAuth.AuthStateListener authStateListener;

    // Views
    private ImageView profileImageView;
    private TextView applicationIdTextView, lastCallDateTextView, lastUpdatedTextView;
    private TextInputLayout nameLayout, emailLayout, phoneLayout, cityLayout, stateLayout, countryLayout, neetScoreLayout;
    private TextInputEditText editName, editEmail, editPhone, editCity, editState, editCountry, editNeetScore;
    private SwitchMaterial switchNeetScore, switchPassport, switchAdmitted;
    private Button btnEditProfile;
   private AppCompatButton btnSave, btnCancel;
    private ProgressBar progressBar;
    private NavController navController;

    // User data
    private String userId;
    private Uri imageUri;
    private String localImagePath;
    private SharedPreferences sharedPreferences;
    private boolean isImageChanged = false;
    private boolean isEditing = false;
    private String firestoreImageUrl;
    private Bitmap selectedImageBitmap;

    // Handler for UI updates
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        authStateListener = firebaseAuth -> {
            FirebaseUser currentUser = firebaseAuth.getCurrentUser();
            if (currentUser == null) {
                Log.e(TAG, "User signed out");
                CustomToast.showToast(requireActivity(), getString(R.string.error_no_user));
                navigateToLogin();
            } else {
                userId = currentUser.getUid();
                Log.d(TAG, "Auth state changed: userId=" + userId);
            }
        };
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_details, container, false);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
        mStorage = FirebaseStorage.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            userId = currentUser.getUid();
            Log.d(TAG, "User ID initialized: " + userId + ", Email: " + currentUser.getEmail());
        } else {
            Log.e(TAG, "No authenticated user found");
            CustomToast.showToast(requireActivity(), getString(R.string.error_no_user));
            userId = null;
            return view;
        }

        // Initialize SharedPreferences
        sharedPreferences = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        // Initialize NavController
        navController = NavHostFragment.findNavController(this);

        // Initialize views
        initViews(view);

        // Load user data
        loadUserData();

        return view;
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
        profileImageView = view.findViewById(R.id.profile_image);
        applicationIdTextView = view.findViewById(R.id.application_id);
        nameLayout = view.findViewById(R.id.name_layout);
        emailLayout = view.findViewById(R.id.email_layout);
        phoneLayout = view.findViewById(R.id.phone_layout);
        cityLayout = view.findViewById(R.id.city_layout);
        stateLayout = view.findViewById(R.id.state_layout);
        countryLayout = view.findViewById(R.id.country_layout);
        neetScoreLayout = view.findViewById(R.id.neet_score_layout);
        editName = view.findViewById(R.id.edit_name);
        editEmail = view.findViewById(R.id.edit_email);
        editPhone = view.findViewById(R.id.edit_phone);
        editCity = view.findViewById(R.id.edit_city);
        editState = view.findViewById(R.id.edit_state);
        editCountry = view.findViewById(R.id.edit_country);
        editNeetScore = view.findViewById(R.id.edit_neet_score);
        switchNeetScore = view.findViewById(R.id.switch_neet_score);
        switchPassport = view.findViewById(R.id.switch_passport);
        switchAdmitted = view.findViewById(R.id.switch_admitted);
        btnEditProfile = view.findViewById(R.id.btn_edit_profile);
        btnSave = view.findViewById(R.id.btn_save);
        btnCancel = view.findViewById(R.id.btn_cancel);
        lastCallDateTextView = view.findViewById(R.id.text_last_call_date);
        lastUpdatedTextView = view.findViewById(R.id.text_last_updated);
        progressBar = view.findViewById(R.id.progressBar);

        // Set initial state of EditTexts and Switches to uneditable
        setFieldsNonEditable();

        // Set listeners
        profileImageView.setOnClickListener(v -> {
            if (isEditing) {
                selectImage();
            }
        });
        btnEditProfile.setOnClickListener(v -> enableEditing());
        btnSave.setOnClickListener(v -> saveUserData());
        btnCancel.setOnClickListener(v -> {
            isEditing = false;
            setFieldsNonEditable();
            loadUserData(); // Reload to reset changes
        });
        switchNeetScore.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isEditing) {
                neetScoreLayout.setVisibility(isChecked ? View.VISIBLE : View.GONE);
                if (!isChecked) {
                    editNeetScore.setText("");
                }
            }
        });
    }

    private void setFieldsEditable() {
        editName.setEnabled(true);
        editEmail.setEnabled(true);
        editPhone.setEnabled(true);
        editCity.setEnabled(true);
        editState.setEnabled(true);
        editCountry.setEnabled(true);
        switchNeetScore.setEnabled(true);
        switchPassport.setEnabled(true);
        switchAdmitted.setEnabled(true);
        editNeetScore.setEnabled(switchNeetScore.isChecked());
        btnSave.setVisibility(View.VISIBLE);
        btnCancel.setVisibility(View.VISIBLE);
        btnEditProfile.setVisibility(View.GONE);
    }

    private void setFieldsNonEditable() {
        editName.setEnabled(false);
        editEmail.setEnabled(false);
        editPhone.setEnabled(false);
        editCity.setEnabled(false);
        editState.setEnabled(false);
        editCountry.setEnabled(false);
        switchNeetScore.setEnabled(false);
        switchPassport.setEnabled(false);
        switchAdmitted.setEnabled(false);
        editNeetScore.setEnabled(false);
        btnSave.setVisibility(View.GONE);
        btnCancel.setVisibility(View.GONE);
        btnEditProfile.setVisibility(View.VISIBLE);
    }

    private void enableEditing() {
        isEditing = true;
        setFieldsEditable();
    }

    private void loadUserData() {
        showLoading(true);
        EncryptedSharedPreferencesManager prefs = new EncryptedSharedPreferencesManager(requireActivity());

        // Try loading from SharedPreferences first
        String email = prefs.getString("email", "");
        String name = prefs.getString("name", "");
        String phone = prefs.getString("mobile", "");
        String city = prefs.getString("city", "");
        String state = prefs.getString("state", "");
        String country = prefs.getString("country", "");
        boolean hasNeetScore = prefs.getBoolean("hasNeetScore", false);
        String neetScore = prefs.getString("neetScore", "");
        boolean hasPassport = prefs.getBoolean("hasPassport", false);
        boolean isAdmitted = prefs.getBoolean("isAdmitted", false);
        String lastCallDate = prefs.getString("lastCallDate", "");
        String lastUpdated = prefs.getString("lastUpdatedDate", "");
        String applicationId = prefs.getString("userId", "");
        firestoreImageUrl = prefs.getString("imageUrl", "");
        localImagePath = prefs.getString("FilePath", "");

        boolean hasLocalData = !email.isEmpty() || !name.isEmpty() || !phone.isEmpty();

        // Load profile image
        if (localImagePath != null && !localImagePath.isEmpty()) {
            File localFile = new File(localImagePath);
            if (localFile.exists()) {
                Glide.with(this)
                        .load(localFile)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true)
                        .circleCrop()
                        .placeholder(R.drawable.logo_doctor_cubes_dark)
                        .error(R.drawable.logo_doctor_cubes_dark)
                        .into(profileImageView);
            } else if (!firestoreImageUrl.isEmpty()) {
                Glide.with(this)
                        .load(firestoreImageUrl)
                        .diskCacheStrategy(DiskCacheStrategy.DATA)
                        .circleCrop()
                        .placeholder(R.drawable.logo_doctor_cubes_dark)
                        .error(R.drawable.logo_doctor_cubes_dark)
                        .into(profileImageView);
            }
        }

        if (hasLocalData) {
            applicationIdTextView.setText(applicationId);
            editName.setText(name);
            editEmail.setText(email);
            editPhone.setText(phone);
            editCity.setText(city);
            editState.setText(state);
            editCountry.setText(country);
            switchNeetScore.setChecked(hasNeetScore);
            neetScoreLayout.setVisibility(hasNeetScore ? View.VISIBLE : View.GONE);
            editNeetScore.setText(neetScore);
            switchPassport.setChecked(hasPassport);
            switchAdmitted.setChecked(isAdmitted);
            lastCallDateTextView.setText(lastCallDate.isEmpty() ? getString(R.string.not_called_yet) : lastCallDate);
            lastUpdatedTextView.setText(lastUpdated.isEmpty() ? getString(R.string.default_last_updated) : lastUpdated);
            showLoading(false);
        } else {
            fetchUserDataFromFirestore();
        }
    }

    private void fetchUserDataFromFirestore() {
        if (userId == null) {
            showLoading(false);
            CustomToast.showToast(requireActivity(), getString(R.string.error_no_user));
            return;
        }

        // Fetch user data
        mFirestore.collection("Users").document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Map<String, Object> userData = documentSnapshot.getData();
                        saveToSharedPreferences(userData);
                        updateUI(userData);
                        showLoading(false);
                    } else {
                        showLoading(false);
                        CustomToast.showToast(requireActivity(), getString(R.string.error_loading));
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error fetching user data: ", e);
                    showLoading(false);
                    CustomToast.showToast(requireActivity(), "Failed to load profile");
                });

        // Fetch application submission data
        mFirestore.collection("app_submissions").document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Map<String, Object> appData = documentSnapshot.getData();
                        saveToSharedPreferences(appData);
                        updateUI(appData);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error fetching application data: ", e);
                });
    }

    private void saveToSharedPreferences(Map<String, Object> data) {
        if (data == null) return;

        EncryptedSharedPreferencesManager prefs = new EncryptedSharedPreferencesManager(requireActivity());
        if (data.containsKey("email")) prefs.putString("email", (String) data.get("email"));
        if (data.containsKey("name")) prefs.putString("name", (String) data.get("name"));
        if (data.containsKey("mobile")) prefs.putString("mobile", (String) data.get("mobile"));
        if (data.containsKey("city")) prefs.putString("city", (String) data.get("city"));
        if (data.containsKey("state")) prefs.putString("state", (String) data.get("state"));
        if (data.containsKey("country")) prefs.putString("country", (String) data.get("country"));
        if (data.containsKey("hasNeetScore")) prefs.putBoolean("hasNeetScore", (Boolean) data.get("hasNeetScore"));
        if (data.containsKey("neetScore")) prefs.putString("neetScore", (String) data.get("neetScore"));
        if (data.containsKey("hasPassport")) prefs.putBoolean("hasPassport", (Boolean) data.get("hasPassport"));
        if (data.containsKey("isAdmitted")) prefs.putBoolean("isAdmitted", (Boolean) data.get("isAdmitted"));
        if (data.containsKey("lastCallDate")) prefs.putString("lastCallDate", (String) data.get("lastCallDate"));
        if (data.containsKey("lastUpdatedDate")) prefs.putString("lastUpdatedDate", (String) data.get("lastUpdatedDate"));
        if (data.containsKey("userId")) prefs.putString("userId", (String) data.get("userId"));
        if (data.containsKey("imageUrl")) {
            prefs.putString("imageUrl", (String) data.get("imageUrl"));
            firestoreImageUrl = (String) data.get("imageUrl");
        }
    }

    private void updateUI(Map<String, Object> data) {
        if (data == null) return;

        if (data.containsKey("userId")) applicationIdTextView.setText((String) data.get("userId"));
        if (data.containsKey("name")) editName.setText((String) data.get("name"));
        if (data.containsKey("email")) editEmail.setText((String) data.get("email"));
        if (data.containsKey("mobile")) editPhone.setText((String) data.get("mobile"));
        if (data.containsKey("city")) editCity.setText((String) data.get("city"));
        if (data.containsKey("state")) editState.setText((String) data.get("state"));
        if (data.containsKey("country")) editCountry.setText((String) data.get("country"));
        if (data.containsKey("hasNeetScore")) {
            boolean hasNeetScore = (Boolean) data.get("hasNeetScore");
            switchNeetScore.setChecked(hasNeetScore);
            neetScoreLayout.setVisibility(hasNeetScore ? View.VISIBLE : View.GONE);
        }
        if (data.containsKey("neetScore")) editNeetScore.setText((String) data.get("neetScore"));
        if (data.containsKey("hasPassport")) switchPassport.setChecked((Boolean) data.get("hasPassport"));
        if (data.containsKey("isAdmitted")) switchAdmitted.setChecked((Boolean) data.get("isAdmitted"));
        if (data.containsKey("lastCallDate")) {
            String lastCallDate = (String) data.get("lastCallDate");
            lastCallDateTextView.setText(lastCallDate.isEmpty() ? getString(R.string.not_called_yet) : lastCallDate);
        }
        if (data.containsKey("lastUpdatedDate")) {
            String lastUpdated = (String) data.get("lastUpdatedDate");
            lastUpdatedTextView.setText(lastUpdated.isEmpty() ? getString(R.string.default_last_updated) : lastUpdated);
        }
        if (data.containsKey("imageUrl") && !((String) data.get("imageUrl")).isEmpty()) {
            Glide.with(this)
                    .load((String) data.get("imageUrl"))
                    .diskCacheStrategy(DiskCacheStrategy.DATA)
                    .circleCrop()
                    .placeholder(R.drawable.logo_doctor_cubes_dark)
                    .error(R.drawable.logo_doctor_cubes_dark)
                    .into(profileImageView);
            saveImageToLocalStorage((String) data.get("imageUrl"));
        }
    }

    private void selectImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, GALLERY_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            imageUri = data.getData();
            isImageChanged = true;
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), imageUri);
                selectedImageBitmap = resizeBitmap(bitmap, 1024, 1024);
                profileImageView.setImageBitmap(selectedImageBitmap);
                saveImageToLocalStorage(selectedImageBitmap);
            } catch (IOException e) {
                Log.e(TAG, "Error loading image from URI: ", e);
                CustomToast.showToast(requireActivity(), getString(R.string.error_loading));
            }
        }
    }

    private Bitmap resizeBitmap(Bitmap original, int maxWidth, int maxHeight) {
        int width = original.getWidth();
        int height = original.getHeight();
        if (width <= maxWidth && height <= maxHeight) {
            return original;
        }
        float ratio = Math.min((float) maxWidth / width, (float) maxHeight / height);
        int newWidth = Math.round(width * ratio);
        int newHeight = Math.round(height * ratio);
        return Bitmap.createScaledBitmap(original, newWidth, newHeight, true);
    }

    private void saveImageToLocalStorage(Bitmap bitmap) throws IOException {
        File outputDir = requireContext().getFilesDir();
        File outputFile = new File(outputDir, userId + "_profile.jpg");
        try (FileOutputStream fos = new FileOutputStream(outputFile)) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, fos);
            localImagePath = outputFile.getAbsolutePath();
            EncryptedSharedPreferencesManager prefs = new EncryptedSharedPreferencesManager(requireContext());
            prefs.putString("FilePath", localImagePath);
        }
    }

    private void saveImageToLocalStorage(String imageUrl) {
        Glide.with(this)
                .asBitmap()
                .load(imageUrl)
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

    private void saveUserData() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            showLoading(false);
            CustomToast.showToast(requireActivity(), getString(R.string.error_no_user));
            navigateToLogin();
            return;
        }

        String name = editName.getText().toString().trim();
        String email = editEmail.getText().toString().trim();
        String phone = editPhone.getText().toString().trim();
        String city = editCity.getText().toString().trim();
        String state = editState.getText().toString().trim();
        String country = editCountry.getText().toString().trim();
        boolean hasNeetScore = switchNeetScore.isChecked();
        String neetScore = hasNeetScore ? editNeetScore.getText().toString().trim() : "";
        boolean hasPassport = switchPassport.isChecked();
        boolean isAdmitted = switchAdmitted.isChecked();

        // Validation
        if (name.isEmpty()) {
            nameLayout.setError("Name cannot be empty");
            return;
        }
        if (email.isEmpty()) {
            emailLayout.setError("Email cannot be empty");
            return;
        }
        if (phone.isEmpty()) {
            phoneLayout.setError("Phone cannot be empty");
            return;
        }
        if (city.isEmpty()) {
            cityLayout.setError("City cannot be empty");
            return;
        }
        if (state.isEmpty()) {
            stateLayout.setError("State cannot be empty");
            return;
        }
        if (country.isEmpty()) {
            countryLayout.setError("Country cannot be empty");
            return;
        }
        if (hasNeetScore && neetScore.isEmpty()) {
            neetScoreLayout.setError("NEET score cannot be empty");
            return;
        }

        // Clear errors
        nameLayout.setError(null);
        emailLayout.setError(null);
        phoneLayout.setError(null);
        cityLayout.setError(null);
        stateLayout.setError(null);
        countryLayout.setError(null);
        neetScoreLayout.setError(null);

        showLoading(true);

        // Save to SharedPreferences
        EncryptedSharedPreferencesManager prefs = new EncryptedSharedPreferencesManager(requireActivity());
        prefs.putString("name", name);
        prefs.putString("email", email);
        prefs.putString("mobile", phone);
        prefs.putString("city", city);
        prefs.putString("state", state);
        prefs.putString("country", country);
        prefs.putBoolean("hasNeetScore", hasNeetScore);
        prefs.putString("neetScore", neetScore);
        prefs.putBoolean("hasPassport", hasPassport);
        prefs.putBoolean("isAdmitted", isAdmitted);
        prefs.putString("userId", userId);
        prefs.putString("lastCallDate", lastCallDateTextView.getText().toString());
        prefs.putString("lastUpdatedDate", lastUpdatedTextView.getText().toString());

        // Prepare Firestore updates
        Map<String, Object> userUpdates = new HashMap<>();
        userUpdates.put("name", name);
        userUpdates.put("email", email);
        userUpdates.put("mobile", phone);

        Map<String, Object> appUpdates = new HashMap<>();
        appUpdates.put("city", city);
        appUpdates.put("state", state);
        appUpdates.put("country", country);
        appUpdates.put("hasNeetScore", hasNeetScore);
        appUpdates.put("neetScore", neetScore);
        appUpdates.put("hasPassport", hasPassport);
        appUpdates.put("isAdmitted", isAdmitted);
        appUpdates.put("email", email);
        appUpdates.put("mobile", phone);
        appUpdates.put("name", name);
        appUpdates.put("userId", userId);

        if (isImageChanged && selectedImageBitmap != null) {
            uploadImageToFirebaseStorage(selectedImageBitmap, userUpdates, appUpdates);
        } else {
            userUpdates.put("imageUrl", firestoreImageUrl != null ? firestoreImageUrl : "");
            appUpdates.put("imageUrl", firestoreImageUrl != null ? firestoreImageUrl : "");
            updateUserData(userUpdates, appUpdates);
        }
    }

    private void uploadImageToFirebaseStorage(Bitmap bitmap, Map<String, Object> userUpdates, Map<String, Object> appUpdates) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null || !currentUser.getUid().equals(userId)) {
            showLoading(false);
            Log.e(TAG, "Authentication error");
            CustomToast.showToast(requireActivity(), getString(R.string.error_no_user));
            navigateToLogin();
            return;
        }

        currentUser.getIdToken(true)
                .addOnSuccessListener(result -> {
                    StorageReference storageRef = mStorage.getReference().child("profile_images/" + userId + "/profile.jpg");
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos);
                    byte[] data = baos.toByteArray();

                    storageRef.putBytes(data)
                            .addOnSuccessListener(taskSnapshot -> storageRef.getDownloadUrl()
                                    .addOnSuccessListener(uri -> {
                                        String imageUrl = uri.toString();
                                        userUpdates.put("imageUrl", imageUrl);
                                        appUpdates.put("imageUrl", imageUrl);
                                        EncryptedSharedPreferencesManager prefs = new EncryptedSharedPreferencesManager(requireContext());
                                        prefs.putString("imageUrl", imageUrl);
                                        updateUserData(userUpdates, appUpdates);
                                        CustomToast.showToast(requireActivity(), getString(R.string.profile_image_updated));
                                    })
                                    .addOnFailureListener(e -> {
                                        showLoading(false);
                                        Log.e(TAG, "Error getting download URL: ", e);
                                        CustomToast.showToast(requireActivity(), getString(R.string.error_image_upload));
                                    }))
                            .addOnFailureListener(e -> {
                                showLoading(false);
                                Log.e(TAG, "Error uploading image: ", e);
                                CustomToast.showToast(requireActivity(), getString(R.string.error_image_upload));
                            });
                })
                .addOnFailureListener(e -> {
                    showLoading(false);
                    Log.e(TAG, "Error refreshing auth token: ", e);
                    CustomToast.showToast(requireActivity(), getString(R.string.error_no_user));
                    navigateToLogin();
                });
    }

    private void updateUserData(Map<String, Object> userUpdates, Map<String, Object> appUpdates) {
        if (userId == null) {
            showLoading(false);
            CustomToast.showToast(requireActivity(), getString(R.string.error_no_user));
            navigateToLogin();
            return;
        }

        // Update Users collection
        mFirestore.collection("Users").document(userId)
                .update(userUpdates)
                .addOnSuccessListener(aVoid -> {
                    // Update app_submissions collection
                    mFirestore.collection("app_submissions").document(userId)
                            .set(appUpdates)
                            .addOnSuccessListener(aVoid1 -> {
                                showLoading(false);
                                CustomToast.showToast(requireActivity(), "Profile updated successfully");
                                isImageChanged = false;
                                isEditing = false;
                                setFieldsNonEditable();
                            })
                            .addOnFailureListener(e -> {
                                showLoading(false);
                                Log.e(TAG, "Error updating application data in Firestore: ", e);
                                CustomToast.showToast(requireActivity(), "Failed to update profile");
                            });
                })
                .addOnFailureListener(e -> {
                    showLoading(false);
                    Log.e(TAG, "Error updating user data in Firestore: ", e);
                    CustomToast.showToast(requireActivity(), "Failed to update profile");
                });
    }

    private void showChangePasswordDialog() {
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_change_password, null);

        TextInputLayout currentPasswordLayout = dialogView.findViewById(R.id.currentPasswordLayout);
        TextInputLayout newPasswordLayout = dialogView.findViewById(R.id.newPasswordLayout);
        TextInputLayout confirmPasswordLayout = dialogView.findViewById(R.id.confirmPasswordLayout);

        TextInputEditText currentPasswordEditText = dialogView.findViewById(R.id.currentPasswordEditText);
        TextInputEditText newPasswordEditText = dialogView.findViewById(R.id.newPasswordEditText);
        TextInputEditText confirmPasswordEditText = dialogView.findViewById(R.id.confirmPasswordEditText);

        MaterialButton cancelButton = dialogView.findViewById(R.id.cancelButton);
        MaterialButton changePasswordButton = dialogView.findViewById(R.id.changePasswordButton);

        FrameLayout animationContainer = dialogView.findViewById(R.id.animationContainer);
        LottieAnimationView successAnimationView = dialogView.findViewById(R.id.successAnimationView);
        LottieAnimationView failureAnimationView = dialogView.findViewById(R.id.failureAnimationView);

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setView(dialogView);
        builder.setTitle(null);

        final AlertDialog dialog = builder.create();

        newPasswordEditText.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(android.text.Editable s) {
                String password = s.toString();
                if (!password.isEmpty()) {
                    if (password.length() < 8) {
                        newPasswordLayout.setError("Password must be at least 8 characters");
                    } else if (!containsLetterAndDigit(password)) {
                        newPasswordLayout.setError("Password must contain both letters and numbers");
                    } else {
                        newPasswordLayout.setError(null);
                    }
                } else {
                    newPasswordLayout.setError(null);
                }
            }
        });

        confirmPasswordEditText.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(android.text.Editable s) {
                String confirmPassword = s.toString();
                String newPassword = newPasswordEditText.getText().toString();
                if (!confirmPassword.isEmpty() && !confirmPassword.equals(newPassword)) {
                    confirmPasswordLayout.setError("Passwords do not match");
                } else {
                    confirmPasswordLayout.setError(null);
                }
            }
        });

        cancelButton.setOnClickListener(v -> dialog.dismiss());

        changePasswordButton.setOnClickListener(v -> {
            if (validateInputs(currentPasswordLayout, newPasswordLayout, confirmPasswordLayout)) {
                String currentPassword = Objects.requireNonNull(currentPasswordEditText.getText()).toString().trim();
                String newPassword = Objects.requireNonNull(newPasswordEditText.getText()).toString().trim();

                setInputsEnabled(false, currentPasswordLayout, newPasswordLayout,
                        confirmPasswordLayout, cancelButton, changePasswordButton);

                changePassword(currentPassword, newPassword, dialog,
                        animationContainer, successAnimationView, failureAnimationView,
                        currentPasswordLayout, newPasswordLayout, confirmPasswordLayout,
                        cancelButton, changePasswordButton);
            }
        });

        dialog.show();
    }

    private boolean containsLetterAndDigit(String password) {
        boolean hasLetter = false;
        boolean hasDigit = false;

        for (char c : password.toCharArray()) {
            if (Character.isLetter(c)) {
                hasLetter = true;
            } else if (Character.isDigit(c)) {
                hasDigit = true;
            }

            if (hasLetter && hasDigit) {
                return true;
            }
        }

        return false;
    }

    private boolean validateInputs(TextInputLayout currentPasswordLayout,
                                   TextInputLayout newPasswordLayout,
                                   TextInputLayout confirmPasswordLayout) {
        boolean isValid = true;

        if (currentPasswordLayout.getEditText().getText().toString().trim().isEmpty()) {
            currentPasswordLayout.setError("Please enter your current password");
            isValid = false;
        } else {
            currentPasswordLayout.setError(null);
        }

        String newPassword = newPasswordLayout.getEditText().getText().toString().trim();
        if (newPassword.isEmpty()) {
            newPasswordLayout.setError("Please enter a new password");
            isValid = false;
        } else if (newPassword.length() < 8) {
            newPasswordLayout.setError("Password must be at least 8 characters");
            isValid = false;
        } else if (!containsLetterAndDigit(newPassword)) {
            newPasswordLayout.setError("Password must contain both letters and numbers");
            isValid = false;
        } else {
            newPasswordLayout.setError(null);
        }

        String confirmPassword = confirmPasswordLayout.getEditText().getText().toString().trim();
        if (!confirmPassword.equals(newPassword)) {
            confirmPasswordLayout.setError("Passwords do not match");
            isValid = false;
        } else {
            confirmPasswordLayout.setError(null);
        }

        return isValid;
    }

    private void setInputsEnabled(boolean enabled, TextInputLayout currentPasswordLayout,
                                  TextInputLayout newPasswordLayout,
                                  TextInputLayout confirmPasswordLayout,
                                  MaterialButton cancelButton,
                                  MaterialButton changePasswordButton) {
        currentPasswordLayout.setEnabled(enabled);
        newPasswordLayout.setEnabled(enabled);
        confirmPasswordLayout.setEnabled(enabled);
        cancelButton.setEnabled(enabled);
        changePasswordButton.setEnabled(enabled);
    }

    private void changePassword(String currentPassword, String newPassword, AlertDialog dialog,
                                FrameLayout animationContainer,
                                LottieAnimationView successAnimationView,
                                LottieAnimationView failureAnimationView,
                                TextInputLayout currentPasswordLayout,
                                TextInputLayout newPasswordLayout,
                                TextInputLayout confirmPasswordLayout,
                                MaterialButton cancelButton,
                                MaterialButton changePasswordButton) {
        showLoading(true);
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null && user.getEmail() != null) {
            AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), currentPassword);
            user.reauthenticate(credential)
                    .addOnSuccessListener(aVoid -> {
                        user.updatePassword(newPassword)
                                .addOnSuccessListener(aVoid1 -> {
                                    showLoading(false);
                                    showSuccessAnimation(dialog, animationContainer, successAnimationView);
                                })
                                .addOnFailureListener(e -> {
                                    showLoading(false);
                                    Log.e(TAG, "Error updating password: ", e);
                                    showFailureAnimation(dialog, animationContainer, failureAnimationView,
                                            currentPasswordLayout, newPasswordLayout, confirmPasswordLayout,
                                            cancelButton, changePasswordButton);
                                });
                    })
                    .addOnFailureListener(e -> {
                        showLoading(false);
                        Log.e(TAG, "Error reauthenticating user: ", e);
                        CustomToast.showToast(requireActivity(), "Failed to authenticate");
                        showFailureAnimation(dialog, animationContainer, failureAnimationView,
                                currentPasswordLayout, newPasswordLayout, confirmPasswordLayout,
                                cancelButton, changePasswordButton);
                    });
        } else {
            showLoading(false);
            CustomToast.showToast(requireActivity(), getString(R.string.error_no_user));
            navigateToLogin();
        }
    }

    private void showSuccessAnimation(AlertDialog dialog,
                                      FrameLayout animationContainer,
                                      LottieAnimationView successAnimationView) {
        animationContainer.setVisibility(View.VISIBLE);
        successAnimationView.setVisibility(View.VISIBLE);
        successAnimationView.playAnimation();

        successAnimationView.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {}

            @Override
            public void onAnimationEnd(Animator animation) {
                new AlertDialog.Builder(requireContext())
                        .setTitle("Success")
                        .setMessage("Your password has been successfully updated.")
                        .setPositiveButton("OK", (dialogInterface, which) -> {
                            dialog.dismiss();
                            mainHandler.post(() -> {
                                if (isAdded() && navController != null) {
                                    navigateToHome();
                                }
                            });
                        })
                        .setCancelable(false)
                        .show();
            }

            @Override
            public void onAnimationCancel(Animator animation) {}

            @Override
            public void onAnimationRepeat(Animator animation) {}
        });
    }

    private void showFailureAnimation(AlertDialog dialog,
                                      FrameLayout animationContainer,
                                      LottieAnimationView failureAnimationView,
                                      TextInputLayout currentPasswordLayout,
                                      TextInputLayout newPasswordLayout,
                                      TextInputLayout confirmPasswordLayout,
                                      MaterialButton cancelButton,
                                      MaterialButton changePasswordButton) {
        animationContainer.setVisibility(View.VISIBLE);
        failureAnimationView.setVisibility(View.VISIBLE);
        failureAnimationView.playAnimation();

        failureAnimationView.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {}

            @Override
            public void onAnimationEnd(Animator animation) {
                animationContainer.setVisibility(View.GONE);
                setInputsEnabled(true, currentPasswordLayout, newPasswordLayout,
                        confirmPasswordLayout, cancelButton, changePasswordButton);
                new AlertDialog.Builder(requireContext())
                        .setTitle("Error")
                        .setMessage("Failed to update password. Please try again.")
                        .setPositiveButton("OK", (dialogInterface, which) -> dialogInterface.dismiss())
                        .show();
            }

            @Override
            public void onAnimationCancel(Animator animation) {}

            @Override
            public void onAnimationRepeat(Animator animation) {}
        });
    }

    private void showLoading(boolean isLoading) {
        if (isAdded()) {
            mainHandler.post(() -> {
              //  progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
                btnEditProfile.setEnabled(!isLoading);
                btnSave.setEnabled(!isLoading);
                btnCancel.setEnabled(!isLoading);
            });
        }
    }

    private void navigateToHome() {
        if (navController != null && navController.getCurrentDestination() != null) {
            try {
               // navController.navigate(R.id.action_fragmentEditDetails_to_settingsHome);
            } catch (Exception e) {
                Log.e(TAG, "Navigation error: ", e);
                CustomToast.showToast(requireActivity(), getString(R.string.error_navigation));
            }
        }
    }

    private void navigateToLogin() {
        if (navController != null && navController.getCurrentDestination() != null) {
            try {
               // navController.navigate(R.id.action_fragmentEditDetails_to_loginFragment);
            } catch (Exception e) {
                Log.e(TAG, "Navigation error: ", e);
                CustomToast.showToast(requireActivity(), getString(R.string.error_navigation));
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}