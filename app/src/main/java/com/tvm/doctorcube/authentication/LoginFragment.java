package com.tvm.doctorcube.authentication;

import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button; // Import for Button
import android.widget.EditText; // Import for EditText
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast; // Import for Toast
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import com.tvm.doctorcube.CustomToast;
import com.tvm.doctorcube.R;
import com.tvm.doctorcube.authentication.datamanager.EncryptedSharedPreferencesManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment; // Import for BottomSheetDialogFragment
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.Objects;
import java.util.regex.Pattern;

public class LoginFragment extends Fragment {

    private FirebaseAuth mAuth;
    private FirebaseFirestore firestoreDB;
    private NavController navController;

    private TextInputEditText accountField;
    private TextInputEditText passwordField;
    private MaterialButton loginButton;
    private MaterialButton getOtpButton;
    private TextView forgotPasswordText;
    private ImageButton backButton;
    private TextInputLayout accountInputLayout;
    private TextInputLayout passwordInputLayout;
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\+?[0-9]{10,13}$");
    private EncryptedSharedPreferencesManager encryptedSharedPreferencesManager;


    @Override
    public void onStart() {
        super.onStart();
        // Check if the user is already logged in on app start
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            // If a user is signed in, try to fetch their data and navigate
            fetchUserDataAndNavigate(currentUser);
        } else {
            // If no user is signed in, ensure SharedPreferences related to login are cleared
            clearLoginSession();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        firestoreDB = FirebaseFirestore.getInstance();

        // Initialize UI Elements
        accountInputLayout = view.findViewById(R.id.accountInputLayout);
        accountField = view.findViewById(R.id.accountEditText);
        passwordField = view.findViewById(R.id.passwordEditText);
        passwordInputLayout = view.findViewById(R.id.passwordInputLayout);
        loginButton = view.findViewById(R.id.loginButton);
        forgotPasswordText = view.findViewById(R.id.forgotPasswordText);
        backButton = view.findViewById(R.id.backButton);

        // Initialize UserDataManager

        // Initialize Encrypted SharedPreferences Manager
        encryptedSharedPreferencesManager = new EncryptedSharedPreferencesManager(requireActivity());

        view.findViewById(R.id.createAccountText).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.createAccountFragment2);
            }
        });

        loginButton.setOnClickListener(v -> {
            String mail = Objects.requireNonNull(accountField.getText()).toString().trim();
            String pass = Objects.requireNonNull(passwordField.getText()).toString().trim();

            if (mail.isEmpty() || pass.isEmpty()) {
                CustomToast.showToast(requireActivity(), "Please fill in all fields.");
                return;
            }

            if (Patterns.EMAIL_ADDRESS.matcher(mail).matches()) {
                if (isValidPassword(pass)) {
                    signInWithEmailAndPassword(mail, pass);
                } else {
                    passwordInputLayout.setError("Password must be at least 8 characters and contain at least one letter and one digit");
                }
            } else {
                CustomToast.showToast(requireActivity(), "Please enter a valid email");
            }
        });

        if (forgotPasswordText != null) {
            forgotPasswordText.setOnClickListener(v -> {
                ForgotPasswordBottomSheetDialogFragment forgotPasswordBottomSheet = new ForgotPasswordBottomSheetDialogFragment();
                forgotPasswordBottomSheet.show(getChildFragmentManager(), forgotPasswordBottomSheet.getTag());
            });
        }

        if (backButton != null) {
            backButton.setOnClickListener(v -> {
                navController.popBackStack();
            });
        }



        return view;
    }

    private void signInWithEmailAndPassword(String email, String password) {
        loginButton.setEnabled(false); // Disable button to prevent multiple clicks
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(requireActivity(), task -> {
                    loginButton.setEnabled(true); // Re-enable button
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            fetchUserDataAndNavigate(user);
                        }
                    } else {
                        // If sign in fails, display a message to the user.
                        CustomToast.showToast(requireActivity(), "Authentication failed: " + task.getException().getMessage());
                        passwordInputLayout.setError(null);
                    }
                });
    }

    private void fetchUserDataAndNavigate(FirebaseUser user) {
        firestoreDB.collection("Users").document(user.getUid()).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document != null && document.exists()) {
                    String role = document.getString("role");
                    String phoneNumber = document.getString("mobile");
                    Boolean isPhoneNumberVerified = document.getBoolean("isPhoneNumberVerified");
                    String name = document.getString("name");
                    String email = document.getString("email");
                    Boolean isFormSubmitted = document.getBoolean("isFormSubmitted");

                    // Storing The Data On The Shared Preferences
                    encryptedSharedPreferencesManager.putString("name", name);
                    encryptedSharedPreferencesManager.putString("email", email);
                    encryptedSharedPreferencesManager.putString("mobile", phoneNumber);
                    encryptedSharedPreferencesManager.putBoolean("isLogin", true);
                    encryptedSharedPreferencesManager.putBoolean("isNumberVerified", Boolean.TRUE.equals(isPhoneNumberVerified));
                    encryptedSharedPreferencesManager.putString("role", role);
                    encryptedSharedPreferencesManager.putBoolean("isFormSubmitted", Boolean.TRUE.equals(isFormSubmitted));

                    // Navigate based on user role and phone verification status
                    navigateToAppropriateActivity(role, isPhoneNumberVerified, isFormSubmitted);
                } else {
                    CustomToast.showToast(requireActivity(), "User data not found.");
                    // Optionally, sign out the user if data is not found
                    mAuth.signOut();
                    clearLoginSession();
                }
            } else {
                CustomToast.showToast(requireActivity(), "Failed to retrieve user data: " + task.getException().getMessage());
                // Optionally, sign out the user on data retrieval failure
                mAuth.signOut();
                clearLoginSession();
            }
        });
    }

    private void navigateToAppropriateActivity(String role, Boolean isPhoneNumberVerified, Boolean isFormSubmitted) {
        NavController navController1 = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
        if (Boolean.TRUE.equals(isPhoneNumberVerified)) {
            if (role != null) {
                if (role.equals("User")) {
                    if (Boolean.TRUE.equals(isFormSubmitted)) {
                        encryptedSharedPreferencesManager.putBoolean("isdataloaded",false);
                        navController1.navigate(R.id.mainActivity2);
                    } else {
                        navController1.navigate(R.id.collectUserDetailsFragment);
                    }
                } else if (role.equals("admin") || role.equals("superadmin")) {
                    navController1.navigate(R.id.adminActivity2);
                } else {
                    CustomToast.showToast(requireActivity(), "Role is not recognized. Contact support.");
                    mAuth.signOut();
                    clearLoginSession();
                }
            } else {
                CustomToast.showToast(requireActivity(), "User role not found.");
                mAuth.signOut();
                clearLoginSession();
            }
        } else {
            CustomToast.showToast(requireActivity(), "Please verify your phone number.");
        }
    }

    private boolean isValidPassword(String password) {
        // Example: At least 8 characters, with at least one letter and one digit
        String passwordPattern = "^(?=.*[A-Za-z])(?=.*\\d).{8,}$";
        return password.matches(passwordPattern);
    }

    private void clearLoginSession() {
        encryptedSharedPreferencesManager.clear();
    }

    // Inner class for ForgotPasswordBottomSheetDialogFragment
    public static class ForgotPasswordBottomSheetDialogFragment extends BottomSheetDialogFragment {

        private EditText editTextEmail;
        private MaterialButton resetButton;

        private FirebaseAuth mAuth;

        public ForgotPasswordBottomSheetDialogFragment() {
            // Required empty public constructor
        }

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_forgot_password_bottom_sheet_dialog, container, false);
            editTextEmail = view.findViewById(R.id.editTextForgotPasswordEmail);
            resetButton = view.findViewById(R.id.buttonResetPassword);

            mAuth = FirebaseAuth.getInstance();

            resetButton.setOnClickListener(v -> {
                String email = editTextEmail.getText().toString().trim();

                if (email.isEmpty()) {
                    editTextEmail.setError("Email is required");
                    return;
                }

                if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    editTextEmail.setError("Enter a valid email");
                    return;
                }

                mAuth.sendPasswordResetEmail(email)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(getContext(), "Password reset email sent. Check your inbox.", Toast.LENGTH_SHORT).show();
                                dismiss(); // Close the bottom sheet
                            } else {
                                Toast.makeText(getContext(), "Failed to send reset email: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
            });

            return view;
        }
    }
}

