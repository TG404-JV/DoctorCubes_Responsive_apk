package com.tvm.doctorcube.authentication;


import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import com.tvm.doctorcube.CustomToast;
import com.tvm.doctorcube.R;
import com.tvm.doctorcube.authentication.datamanager.EncryptedSharedPreferencesManager;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.auth.PhoneAuthProvider.OnVerificationStateChangedCallbacks;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class CreateAccountFragment extends Fragment {

    private static final String TAG = "CreateAccountFragment";
    private FirebaseAuth mAuth;
    private FirebaseFirestore firestoreDB;
    private NavController navController;
    private SharedPreferences sharedPreferences;
    private String verificationId;
    private Handler handler = new Handler(Looper.getMainLooper());  // Use main looper
    private OnVerificationStateChangedCallbacks verificationCallbacks;

    // User data to store
    private String fullName, email, phone, password;

    // UI Elements
    private EditText fullNameEditText, emailEditText, phoneEditText, passwordEditText, confirmPasswordEditText, otpEditText;
    private Button createAccountButton, verifyOtpButton, cancelOtpButton;
    private CheckBox termsCheckbox;
    private TextView termsText, loginText, resendOtpText, passwordStrengthText;
    private ProgressBar progressBar;
    private ImageButton backButton;

    // Dialog
    private AlertDialog otpVerificationDialog;

    private static final long OTP_TIMEOUT_MS = 60000; // 60 seconds

    private CountDownTimer otpCountDownTimer;

    public CreateAccountFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_create_account, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        firestoreDB = FirebaseFirestore.getInstance();
        navController = Navigation.findNavController(view);

        // Initialize Views
        fullNameEditText = view.findViewById(R.id.fullNameEditText);
        emailEditText = view.findViewById(R.id.emailEditText);
        phoneEditText = view.findViewById(R.id.phoneEditText);
        passwordEditText = view.findViewById(R.id.passwordEditText);
        confirmPasswordEditText = view.findViewById(R.id.confirmPasswordEditText);
        createAccountButton = view.findViewById(R.id.createAccountButton);
        termsCheckbox = view.findViewById(R.id.termsCheckbox);
        termsText = view.findViewById(R.id.termsText);
        loginText = view.findViewById(R.id.loginText);
        progressBar = view.findViewById(R.id.progressBar);
        backButton = view.findViewById(R.id.backButton);
        passwordStrengthText = view.findViewById(R.id.passwordStrengthText);



        // Set up password visibility toggle
        passwordEditText.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_eye, 0);
        passwordEditText.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (event.getRawX() >= (passwordEditText.getRight() - passwordEditText.getCompoundDrawables()[2].getBounds().width())) {
                    togglePasswordVisibility(passwordEditText);
                    return true;
                }
            }
            return false;
        });

        // Real-time validation
        setupRealTimeValidation();

        // Set Click Listeners
        createAccountButton.setOnClickListener(v -> validateAndProceed());
        loginText.setOnClickListener(v -> navController.navigate(R.id.action_createAccountFragment2_to_loginFragment2));
        termsText.setOnClickListener(v -> showTermsAndConditions());
        backButton.setOnClickListener(v -> navController.navigate(R.id.action_createAccountFragment2_to_loginFragment2));

        // Handle back press
        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener((v, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
                navController.navigate(R.id.action_createAccountFragment2_to_loginFragment2);
                return true;
            }
            return false;
        });

        // Setup Phone Auth Callbacks
        setupPhoneAuthCallbacks();
    }

    private void setupRealTimeValidation() {
        fullNameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().trim().isEmpty()) {
                    fullNameEditText.setError("Enter Full Name");
                } else if (s.toString().matches(".*\\d.*")) {
                    fullNameEditText.setError("Name cannot contain numbers");
                } else {
                    fullNameEditText.setError(null);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

        emailEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if (!android.util.Patterns.EMAIL_ADDRESS.matcher(s.toString().trim()).matches()) {
                    emailEditText.setError("Invalid email format");
                } else {
                    emailEditText.setError(null);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

        phoneEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() != 10 || !s.toString().matches("\\d+")) {
                    phoneEditText.setError("Enter a valid 10-digit phone number");
                } else {
                    phoneEditText.setError(null);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

        passwordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                String password = s.toString().trim();
                if (!password.isEmpty()) {
                    updatePasswordStrength(password);
                    if (!isValidPassword(password)) {
                        passwordEditText.setError("Password must be 8-14 chars with uppercase, lowercase, number, and special char");
                    } else {
                        passwordEditText.setError(null);
                    }
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

        confirmPasswordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                String confirmPassword = s.toString().trim();
                if (!confirmPassword.isEmpty() && !confirmPassword.equals(passwordEditText.getText().toString().trim())) {
                    confirmPasswordEditText.setError("Passwords do not match");
                } else {
                    confirmPasswordEditText.setError(null);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });
    }

    private void validateAndProceed() {
        fullName = fullNameEditText.getText().toString().trim();
        email = emailEditText.getText().toString().trim();
        phone = phoneEditText.getText().toString().trim();
        password = passwordEditText.getText().toString().trim();
        String confirmPassword = confirmPasswordEditText.getText().toString().trim();

        // Validate inputs
        if (TextUtils.isEmpty(fullName) || fullName.matches(".*\\d.*")) {
            fullNameEditText.setError("Enter a valid Full Name");
            shakeField(fullNameEditText);
            return;
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.setError("Enter a valid Email");
            shakeField(emailEditText);
            return;
        }
        if (phone.length() != 10 || !phone.matches("\\d+")) {
            phoneEditText.setError("Enter a valid 10-digit Phone Number");
            shakeField(phoneEditText);
            return;
        }
        if (!isValidPassword(password)) {
            passwordEditText.setError("Password must be 8-14 chars with uppercase, lowercase, number, and special char");
            shakeField(passwordEditText);
            return;
        }
        if (!password.equals(confirmPassword)) {
            confirmPasswordEditText.setError("Passwords do not match");
            shakeField(confirmPasswordEditText);
            return;
        }
        if (!termsCheckbox.isChecked()) {
            CustomToast.showToast(requireActivity(), "Please accept terms and conditions");
            return;
        }

        // Show progress and disable button
        progressBar.setVisibility(View.VISIBLE);
        createAccountButton.setEnabled(false);
        createAccountButton.setAlpha(0.5f);

        // First verify phone number with OTP
        sendOtp("+91" + phone);
    }

    private boolean isValidPassword(String password) {
        if (password == null) return false;
        if (password.length() < 8 || password.length() > 14) return false;
        if (!password.matches(".*[A-Z].*")) return false;
        if (!password.matches(".*[a-z].*")) return false;
        if (!password.matches(".*[0-9].*")) return false;
        if (!password.matches(".*[!@#$%^&*(),.?\":{}|<>].*")) return false;
        return true;
    }

    private void updatePasswordStrength(String password) {
        if (password == null || password.isEmpty()) {
            passwordStrengthText.setText("");
            return;
        }
        if (password.length() < 8) {
            passwordStrengthText.setText("Weak");
            passwordStrengthText.setTextColor(Color.RED);
        } else if (isValidPassword(password)) {
            passwordStrengthText.setText("Strong");
            passwordStrengthText.setTextColor(Color.GREEN);
        } else {
            passwordStrengthText.setText("Medium");
            passwordStrengthText.setTextColor(Color.BLUE);
        }
    }

    private void setupPhoneAuthCallbacks() {
        verificationCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
                // Auto-verification completed, proceed to create email account
                if (otpCountDownTimer != null) {
                    otpCountDownTimer.cancel();
                }
                createEmailAccount(credential);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                progressBar.setVisibility(View.GONE);
                createAccountButton.setEnabled(true);
                createAccountButton.setAlpha(1.0f);
                String message = "Verification failed";
                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    message = "Invalid phone number format.";
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    message = "Too many attempts. Try again later.";
                } else {
                    // Handle the Play Integrity error specifically
                    if (e.getMessage() != null && e.getMessage().contains("Play Integrity checks, and reCAPTCHA checks were unsuccessful")) {
                        message = "This device/app is not authorized. Please try again later.";
                    } else {
                        message = "Phone verification failed: " + e.getMessage(); // General error
                    }
                }
                CustomToast.showToast(requireActivity(), e.getMessage());
            }

            @Override
            public void onCodeSent(@NonNull String verId, @NonNull PhoneAuthProvider.ForceResendingToken token) {
                progressBar.setVisibility(View.GONE);
                createAccountButton.setEnabled(true);
                createAccountButton.setAlpha(1.0f);
                verificationId = verId;
                showOtpVerificationDialog();
                startOtpCountdown();
            }
        };
    }

    private void sendOtp(String phoneNumber) {
        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(mAuth)
                .setPhoneNumber(phoneNumber)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(requireActivity())
                .setCallbacks(verificationCallbacks)
                .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }
    private void showOtpVerificationDialog() {
        // Inflate the dialog layout
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.otp_verification_bottom_sheet, null);

        // Initialize the views within the dialog layout
        otpEditText = dialogView.findViewById(R.id.otpNumberVerification);
        verifyOtpButton = dialogView.findViewById(R.id.verifyOtpButton);
        cancelOtpButton = dialogView.findViewById(R.id.cancelOtpButton);
        resendOtpText = dialogView.findViewById(R.id.resendOtpText);

        // Build the dialog
        otpVerificationDialog = new AlertDialog.Builder(requireContext())
                .setView(dialogView)
                .setCancelable(false) // Prevent dismissing by touching outside
                .create();

        // Set the button click listeners
        verifyOtpButton.setOnClickListener(v -> {
            verifyOtp();
        });
        cancelOtpButton.setOnClickListener(v -> {
            if (otpCountDownTimer != null) {
                otpCountDownTimer.cancel();
            }
            otpVerificationDialog.dismiss();
        });
        resendOtpText.setOnClickListener(v -> {
            resendOtp();
        });

        otpVerificationDialog.show();
    }

    private void verifyOtp() {
        String otp = otpEditText.getText().toString().trim();
        if (TextUtils.isEmpty(otp)) {
            otpEditText.setError("Enter OTP");
            shakeField(otpEditText);
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        verifyOtpButton.setEnabled(false);

        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, otp);
        mAuth.signInWithCredential(credential).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {

                if (otpCountDownTimer != null) {
                    otpCountDownTimer.cancel();
                }
                // Phone verification successful, now sign out and create email account
                mAuth.signOut();
                createEmailAccount(credential);
            } else {
                progressBar.setVisibility(View.GONE);
                verifyOtpButton.setEnabled(true);
                CustomToast.showToast(requireActivity(), "Incorrect OTP");
            }
        });
    }

    private void createEmailAccount(PhoneAuthCredential credential) {
        progressBar.setVisibility(View.VISIBLE);

        // Now create the actual account with email/password
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    progressBar.setVisibility(View.GONE);

                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();

                        if (otpVerificationDialog != null && otpVerificationDialog.isShowing()) {
                            otpVerificationDialog.dismiss();
                        }

                        EncryptedSharedPreferencesManager encryptedSharedPreferencesManager = new EncryptedSharedPreferencesManager(requireContext());

                        encryptedSharedPreferencesManager.putString("name", String.valueOf(fullNameEditText.getText()));
                        encryptedSharedPreferencesManager.putString("email", String.valueOf(emailEditText.getText()));
                        encryptedSharedPreferencesManager.putString("mobile", String.valueOf(phoneEditText.getText()));
                        encryptedSharedPreferencesManager.putBoolean("isLogin", true);
                        encryptedSharedPreferencesManager.putBoolean("isNumberVerified", true);
                        encryptedSharedPreferencesManager.putString("role", "User");

                        Map<String, Object> userData = new HashMap<>();
                        userData.put("name", fullName);
                        userData.put("email", email);
                        userData.put("mobile", phone);
                        userData.put("role", "User");
                        userData.put("isVerified", true);
                        userData.put("isPhoneNumberVerified", true);
                        userData.put("timestamp", FieldValue.serverTimestamp());
                        //CHANGE IS HERE
                        FirebaseFirestore.getInstance().collection("Users").document(user.getUid())
                                .set(userData)
                                .addOnSuccessListener(aVoid -> {
                                    navController.navigate(R.id.collectUserDetailsFragment);
                                })
                                .addOnFailureListener(e -> {
                                    CustomToast.showToast(requireActivity(), "Failed to save user data. Please try again.");
                                    // Consider re-enabling the create account button here if the user needs to retry
                                    createAccountButton.setEnabled(true);
                                    createAccountButton.setAlpha(1.0f);
                                });
                        //CHANGE ENDS HERE

                    } else {
                        // Enable the button in the OTP dialog if it's showing
                        if (otpVerificationDialog != null && otpVerificationDialog.isShowing()) {
                            verifyOtpButton.setEnabled(true);
                        } else {
                            createAccountButton.setEnabled(true);
                            createAccountButton.setAlpha(1.0f);
                        }

                        // Handle specific error cases
                        String errorMessage = "Account creation failed";
                        if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                            errorMessage = "Email already in use.";
                        } else {
                            errorMessage = "Account creation failed: " + task.getException().getMessage();
                        }
                        CustomToast.showToast(requireActivity(), errorMessage);
                        //Re-authenticate the user.
                        if (credential != null && mAuth.getCurrentUser() != null) {
                            mAuth.getCurrentUser().reauthenticate(credential);
                        }
                    }
                });
    }

    private void startOtpCountdown() {
        resendOtpText.setEnabled(false);
        otpCountDownTimer = new CountDownTimer(OTP_TIMEOUT_MS, 1000) {
            public void onTick(long millisUntilFinished) {
                resendOtpText.setText("Resend in " + millisUntilFinished / 1000 + "s");
            }

            public void onFinish() {
                resendOtpText.setText("Resend OTP");
                resendOtpText.setEnabled(true);
                if (otpCountDownTimer != null) {
                    otpCountDownTimer.cancel();
                }
            }
        }.start();
    }

    private void resendOtp() {
        new AlertDialog.Builder(requireContext())
                .setMessage("Resend OTP to " + phone + "?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    if (otpCountDownTimer != null) {
                        otpCountDownTimer.cancel();
                    }
                    sendOtp("+91" + phone);
                    startOtpCountdown();
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void showTermsAndConditions() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Terms and Conditions")
                .setMessage(getString(R.string.terms_and_conditions_createac))
                .setPositiveButton("Accept", (dialog, which) -> termsCheckbox.setChecked(true))
                .setNegativeButton("Decline", null)
                .show();
    }

    private void togglePasswordVisibility(EditText editText) {
        if (editText.getTransformationMethod() == null) {
            editText.setTransformationMethod(android.text.method.PasswordTransformationMethod.getInstance());
        } else {
            editText.setTransformationMethod(null);
        }
        editText.setSelection(editText.getText().length());
    }

    private void shakeField(View view) {
        Animation shake = AnimationUtils.loadAnimation(requireContext(), R.anim.shake);
        view.startAnimation(shake);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (otpCountDownTimer != null) {
            otpCountDownTimer.cancel();
        }
    }
}
