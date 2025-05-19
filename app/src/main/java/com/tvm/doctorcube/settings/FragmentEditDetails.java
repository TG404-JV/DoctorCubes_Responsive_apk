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
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.button.MaterialButton;
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
    private TextInputLayout emailLayout, fullNameLayout, phoneLayout;
    private TextInputEditText emailEditText, fullNameEditText, phoneEditText;
    private MaterialButton saveButton, changePasswordButton;
    private ProgressBar progressBar;
    private Toolbar fragmentToolbar;
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
        fragmentToolbar = view.findViewById(R.id.toolbar);
        if (fragmentToolbar != null) {
            fragmentToolbar.setNavigationOnClickListener(v -> navigateToHome());
        }

        profileImageView = view.findViewById(R.id.profileImageView);
        emailLayout = view.findViewById(R.id.emailLayout);
        fullNameLayout = view.findViewById(R.id.fullNameLayout);
        phoneLayout = view.findViewById(R.id.phoneLayout);
        emailEditText = view.findViewById(R.id.emailEditText);
        fullNameEditText = view.findViewById(R.id.fullNameEditText);
        phoneEditText = view.findViewById(R.id.phoneEditText);
        saveButton = view.findViewById(R.id.saveButton);
        changePasswordButton = view.findViewById(R.id.changePasswordButton);
        progressBar = view.findViewById(R.id.progressBar);

        // Set initial state of EditTexts to uneditable
        setEditTextNonEditable();
        saveButton.setText(R.string.edit_details);

        // Set listeners
        profileImageView.setOnClickListener(v -> {
            if (isEditing) {
                selectImage();
            }
        });
        saveButton.setOnClickListener(v -> {
            if (isEditing) {
                saveUserData();
            } else {
                enableEditing();
            }
        });
        changePasswordButton.setOnClickListener(v -> showChangePasswordDialog());
    }

    private final TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            EncryptedSharedPreferencesManager prefs = new EncryptedSharedPreferencesManager(requireActivity());
            isEditing = !Objects.requireNonNull(emailEditText.getText()).toString().equals(prefs.getString("email", ""))
                    || !Objects.requireNonNull(fullNameEditText.getText()).toString().equals(prefs.getString("name", ""))
                    || !Objects.requireNonNull(phoneEditText.getText()).toString().equals(prefs.getString("phone", ""));
            saveButton.setText(isEditing ? R.string.save_changes : R.string.edit_details);
        }

        @Override
        public void afterTextChanged(Editable s) {}
    };

    private void enableEditing() {
        setEditTextEditable();
        saveButton.setText(R.string.save_changes);
        isEditing = true;
    }

    private void setEditTextEditable() {
        emailEditText.setFocusableInTouchMode(true);
        fullNameEditText.setFocusableInTouchMode(true);
        phoneEditText.setFocusableInTouchMode(true);
        emailEditText.setFocusable(true);
        fullNameEditText.setFocusable(true);
        phoneEditText.setFocusable(true);
        emailEditText.requestFocus();
    }

    private void setEditTextNonEditable() {
        emailEditText.setFocusable(false);
        fullNameEditText.setFocusable(false);
        phoneEditText.setFocusable(false);
        emailEditText.setFocusableInTouchMode(false);
        fullNameEditText.setFocusableInTouchMode(false);
        phoneEditText.setFocusableInTouchMode(false);
    }

    private void loadUserData() {
        showLoading(true);
        EncryptedSharedPreferencesManager prefs = new EncryptedSharedPreferencesManager(requireActivity());

        String email = prefs.getString("email", "");
        String fullName = prefs.getString("name", "");
        String phone = prefs.getString("phone", "");
        firestoreImageUrl = prefs.getString("imageUrl", "");
        localImagePath = prefs.getString("FilePath", "");

        boolean hasLocalData = !email.isEmpty() || !fullName.isEmpty() || !phone.isEmpty();

        if (localImagePath != null && !localImagePath.isEmpty()) {
            File localFile = new File(localImagePath);
            if (localFile.exists()) {
                Glide.with(this)
                        .load(localFile)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true)
                        .circleCrop()
                        .placeholder(R.drawable.logo_doctor_cubes_white)
                        .error(R.drawable.logo_doctor_cubes_white)
                        .into(profileImageView);
            } else if (!firestoreImageUrl.isEmpty()) {
                Glide.with(this)
                        .load(firestoreImageUrl)
                        .diskCacheStrategy(DiskCacheStrategy.DATA)
                        .circleCrop()
                        .placeholder(R.drawable.logo_doctor_cubes_white)
                        .error(R.drawable.logo_doctor_cubes_white)
                        .into(profileImageView);
            }
        }

        if (hasLocalData) {
            emailEditText.setText(email);
            fullNameEditText.setText(fullName);
            phoneEditText.setText(phone);
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
        mFirestore.collection("Users").document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String email = documentSnapshot.getString("email");
                        String fullName = documentSnapshot.getString("name");
                        String phone = documentSnapshot.getString("mobile");
                        firestoreImageUrl = documentSnapshot.getString("imageUrl");

                        email = email != null ? email : getString(R.string.default_email);
                        fullName = fullName != null ? fullName : getString(R.string.default_name);
                        phone = phone != null ? phone : getString(R.string.sample_phone);

                        emailEditText.setText(email);
                        fullNameEditText.setText(fullName);
                        phoneEditText.setText(phone);

                        EncryptedSharedPreferencesManager prefs = new EncryptedSharedPreferencesManager(requireActivity());
                        prefs.putString("email", email);
                        prefs.putString("name", fullName);
                        prefs.putString("phone", phone);
                        prefs.putString("imageUrl", firestoreImageUrl != null ? firestoreImageUrl : "");

                        if (firestoreImageUrl != null && !firestoreImageUrl.isEmpty()) {
                            Glide.with(this)
                                    .load(firestoreImageUrl)
                                    .diskCacheStrategy(DiskCacheStrategy.DATA)
                                    .circleCrop()
                                    .placeholder(R.drawable.logo_doctor_cubes_white)
                                    .error(R.drawable.logo_doctor_cubes_white)
                                    .into(profileImageView);
                            saveImageToLocalStorage(firestoreImageUrl);
                        }

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
        } catch (IOException e) {
            Log.e(TAG, "Error saving image to local storage: ", e);
            throw e;
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

        String email = emailEditText.getText().toString().trim();
        String fullName = fullNameEditText.getText().toString().trim();
        String phone = phoneEditText.getText().toString().trim();

        if (email.isEmpty()) {
            emailLayout.setError("Email cannot be empty");
            return;
        }
        if (fullName.isEmpty()) {
            fullNameLayout.setError("Name cannot be empty");
            return;
        }
        if (phone.isEmpty()) {
            phoneLayout.setError("Phone cannot be empty");
            return;
        }

        emailLayout.setError(null);
        fullNameLayout.setError(null);
        phoneLayout.setError(null);
        showLoading(true);

        EncryptedSharedPreferencesManager prefs = new EncryptedSharedPreferencesManager(requireActivity());
        prefs.putString("email", email);
        prefs.putString("name", fullName);
        prefs.putString("phone", phone);

        Map<String, Object> updates = new HashMap<>();
        updates.put("email", email);
        updates.put("name", fullName);
        updates.put("mobile", phone);

        if (isImageChanged && selectedImageBitmap != null) {
            uploadImageToFirebaseStorage(selectedImageBitmap, updates);
        } else {
            updates.put("imageUrl", firestoreImageUrl != null ? firestoreImageUrl : "");
            updateUserData(updates);
        }

        isEditing = false;
        saveButton.setText(R.string.edit_details);
        setEditTextNonEditable();
    }

    private void uploadImageToFirebaseStorage(Bitmap bitmap, Map<String, Object> updates) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null || !currentUser.getUid().equals(userId)) {
            showLoading(false);
            Log.e(TAG, "Authentication error: currentUser=" + (currentUser != null ? currentUser.getUid() : "null") + ", userId=" + userId);
            CustomToast.showToast(requireActivity(), getString(R.string.error_no_user));
            navigateToLogin();
            return;
        }

        currentUser.getIdToken(true)
                .addOnSuccessListener(result -> {
                    Log.d(TAG, "Auth token refreshed for userId: " + userId);
                    // Corrected storage path: added "/profile.jpg"
                    StorageReference storageRef = mStorage.getReference().child("profile_images/" + userId + "/profile.jpg");
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos);
                    byte[] data = baos.toByteArray();

                    storageRef.putBytes(data)
                            .addOnSuccessListener(taskSnapshot -> storageRef.getDownloadUrl()
                                    .addOnSuccessListener(uri -> {
                                        String imageUrl = uri.toString();
                                        updates.put("imageUrl", imageUrl);
                                        EncryptedSharedPreferencesManager prefs = new EncryptedSharedPreferencesManager(requireContext());
                                        prefs.putString("imageUrl", imageUrl);
                                        updateUserData(updates);
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
                                if (e instanceof com.google.firebase.storage.StorageException && ((com.google.firebase.storage.StorageException) e).getErrorCode() == com.google.firebase.storage.StorageException.ERROR_NOT_AUTHORIZED) {
                                    CustomToast.showToast(requireActivity(), "Permission denied. Please sign in again.");
                                    navigateToLogin();
                                } else {
                                    CustomToast.showToast(requireActivity(), getString(R.string.error_image_upload));
                                }
                            });
                })
                .addOnFailureListener(e -> {
                    showLoading(false);
                    Log.e(TAG, "Error refreshing auth token: ", e);
                    CustomToast.showToast(requireActivity(), getString(R.string.error_no_user));
                    navigateToLogin();
                });
    }

    private void updateUserData(Map<String, Object> updates) {
        if (userId == null) {
            showLoading(false);
            CustomToast.showToast(requireActivity(), getString(R.string.error_no_user));
            navigateToLogin();
            return;
        }
        mFirestore.collection("Users").document(userId)
                .update(updates)
                .addOnSuccessListener(aVoid -> {
                    showLoading(false);
                    CustomToast.showToast(requireActivity(), "Profile updated successfully");
                    isImageChanged = false;
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

        newPasswordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
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

        confirmPasswordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
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
                progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
                saveButton.setEnabled(!isLoading);
                changePasswordButton.setEnabled(!isLoading);
            });
        }
    }

    private void navigateToHome() {
        if (navController != null && navController.getCurrentDestination() != null) {
            try {
                //navController.navigate(R.id.action_fragmentEditDetails_to_settingsHome);
            } catch (Exception e) {
                Log.e(TAG, "Navigation error: ", e);
                CustomToast.showToast(requireActivity(), getString(R.string.error_navigation));
            }
        }
    }

    private void navigateToLogin() {
        if (navController != null && navController.getCurrentDestination() != null) {
            try {
              //  navController.navigate(R.id.action_fragmentEditDetails_to_loginFragment);
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