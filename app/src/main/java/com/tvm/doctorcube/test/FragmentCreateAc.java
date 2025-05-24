package com.tvm.doctorcube.test;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
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
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class FragmentCreateAc extends Fragment {

    private static final String TAG = "CreateAccountFragment";
    private FirebaseAuth mAuth;
    private FirebaseFirestore firestoreDB;
    private NavController navController;
    private String verificationId;
    private Handler handler = new Handler(Looper.getMainLooper());
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks verificationCallbacks;

    // User data to store
    private String fullName, email, phone, countryCode, password;

    // UI Elements
    private TextInputEditText fullNameEditText, emailEditText, phoneEditText, countryCodeEditText, passwordEditText, confirmPasswordEditText, otpEditText;
    private MaterialButton createAccountButton;
    private Button verifyOtpButton, cancelOtpButton;
    private MaterialCheckBox termsCheckbox;
    private TextView loginText, resendOtpText;
    private View passwordStrengthIndicator1, passwordStrengthIndicator2, passwordStrengthIndicator3, passwordStrengthIndicator4;
    private ProgressBar progressBar;

    // Dialog
    private AlertDialog otpVerificationDialog;

    private static final long OTP_TIMEOUT_MS = 60000; // 60 seconds
    private CountDownTimer otpCountDownTimer;

    public FragmentCreateAc() {
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
        countryCodeEditText = view.findViewById(R.id.countryCodeEditText);
        passwordEditText = view.findViewById(R.id.passwordEditText);
        confirmPasswordEditText = view.findViewById(R.id.confirmPasswordEditText);
        createAccountButton = view.findViewById(R.id.createAccountButton);
        termsCheckbox = view.findViewById(R.id.termsCheckbox);
        loginText = view.findViewById(R.id.loginText);
        progressBar = view.findViewById(R.id.progressBar);
        passwordStrengthIndicator1 = view.findViewById(R.id.passwordStrengthIndicator1);
        passwordStrengthIndicator2 = view.findViewById(R.id.passwordStrengthIndicator2);
        passwordStrengthIndicator3 = view.findViewById(R.id.passwordStrengthIndicator3);
        passwordStrengthIndicator4 = view.findViewById(R.id.passwordStrengthIndicator4);

        // Real-time validation
        setupRealTimeValidation();

        // Set Click Listeners
        createAccountButton.setOnClickListener(v -> validateAndProceed());
        loginText.setOnClickListener(v -> navController.navigate(R.id.action_createAccountFragment2_to_loginFragment2));
        termsCheckbox.setOnClickListener(v -> showTermsAndConditions());

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
        if (fullNameEditText == null) Log.e(TAG, "fullNameEditText is null");
        if (emailEditText == null) Log.e(TAG, "emailEditText is null");
        if (phoneEditText == null) Log.e(TAG, "phoneEditText is null");
        if (countryCodeEditText == null) Log.e(TAG, "countryCodeEditText is null");
        if (passwordEditText == null) Log.e(TAG, "passwordEditText is null");
        if (confirmPasswordEditText == null) Log.e(TAG, "confirmPasswordEditText is null");

        if (fullNameEditText != null) {
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
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {}
            });
        }

        if (emailEditText != null) {
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
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {}
            });
        }

        if (countryCodeEditText != null) {
            countryCodeEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void afterTextChanged(Editable s) {
                    if (TextUtils.isEmpty(s.toString().trim()) || !s.toString().startsWith("+")) {
                        countryCodeEditText.setError("Enter valid country code (e.g., +91)");
                    } else {
                        countryCodeEditText.setError(null);
                    }
                }
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {}
            });
        }

        if (phoneEditText != null) {
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
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {}
            });
        }

        if (passwordEditText != null) {
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
                    } else {
                        resetPasswordStrengthIndicators();
                    }
                }
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {}
            });
        }

        if (confirmPasswordEditText != null) {
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
    }
    private void validateAndProceed() {
        fullName = fullNameEditText.getText().toString().trim();
        email = emailEditText.getText().toString().trim();
        phone = phoneEditText.getText().toString().trim();
        countryCode = countryCodeEditText.getText().toString().trim();
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
        if (TextUtils.isEmpty(countryCode) || !countryCode.startsWith("+")) {
            countryCodeEditText.setError("Enter valid country code (e.g., +91)");
            shakeField(countryCodeEditText);
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

        // Send OTP with country code
        sendOtp(countryCode + phone);
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
        resetPasswordStrengthIndicators();
        if (password == null || password.isEmpty()) {
            return;
        }

        int strength = calculatePasswordStrength(password);
        switch (strength) {
            case 1:
                passwordStrengthIndicator1.setBackgroundColor(getResources().getColor(R.color.password_strength_weak));
                break;
            case 2:
                passwordStrengthIndicator1.setBackgroundColor(getResources().getColor(R.color.password_strength_fair));
                passwordStrengthIndicator2.setBackgroundColor(getResources().getColor(R.color.password_strength_fair));
                break;
            case 3:
                passwordStrengthIndicator1.setBackgroundColor(getResources().getColor(R.color.password_strength_strong));
                passwordStrengthIndicator2.setBackgroundColor(getResources().getColor(R.color.password_strength_strong));
                passwordStrengthIndicator3.setBackgroundColor(getResources().getColor(R.color.password_strength_strong));
                break;
            case 4:
                passwordStrengthIndicator1.setBackgroundColor(getResources().getColor(R.color.password_strength_strong));
                passwordStrengthIndicator2.setBackgroundColor(getResources().getColor(R.color.password_strength_strong));
                passwordStrengthIndicator3.setBackgroundColor(getResources().getColor(R.color.password_strength_strong));
                passwordStrengthIndicator4.setBackgroundColor(getResources().getColor(R.color.password_strength_strong));
                break;
        }
    }

    private int calculatePasswordStrength(String password) {
        int strength = 0;
        if (password.length() >= 8) strength++;
        if (password.matches(".*[A-Z].*")) strength++;
        if (password.matches(".*[0-9].*")) strength++;
        if (password.matches(".*[!@#$%^&*(),.?\":{}|<>].*")) strength++;
        return strength;
    }

    private void resetPasswordStrengthIndicators() {
        passwordStrengthIndicator1.setBackgroundColor(getResources().getColor(R.color.password_strength_inactive));
        passwordStrengthIndicator2.setBackgroundColor(getResources().getColor(R.color.password_strength_inactive));
        passwordStrengthIndicator3.setBackgroundColor(getResources().getColor(R.color.password_strength_inactive));
        passwordStrengthIndicator4.setBackgroundColor(getResources().getColor(R.color.password_strength_inactive));
    }

    private void setupPhoneAuthCallbacks() {
        verificationCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
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
                } else if (e.getMessage() != null && e.getMessage().contains("Play Integrity checks, and reCAPTCHA checks were unsuccessful")) {
                    message = "This device/app is not authorized. Please try again later.";
                } else {
                    message = "Phone verification failed: " + e.getMessage();
                }
                CustomToast.showToast(requireActivity(), message);
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
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.otp_verification_bottom_sheet, null);
        otpEditText = dialogView.findViewById(R.id.otpNumberVerification);
        verifyOtpButton = dialogView.findViewById(R.id.verifyOtpButton);
        cancelOtpButton = dialogView.findViewById(R.id.cancelOtpButton);
        resendOtpText = dialogView.findViewById(R.id.resendOtpText);

        otpVerificationDialog = new AlertDialog.Builder(requireContext())
                .setView(dialogView)
                .setCancelable(false)
                .create();

        verifyOtpButton.setOnClickListener(v -> verifyOtp());
        cancelOtpButton.setOnClickListener(v -> {
            if (otpCountDownTimer != null) {
                otpCountDownTimer.cancel();
            }
            otpVerificationDialog.dismiss();
        });
        resendOtpText.setOnClickListener(v -> resendOtp());

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

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    progressBar.setVisibility(View.GONE);

                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();

                        if (otpVerificationDialog != null && otpVerificationDialog.isShowing()) {
                            otpVerificationDialog.dismiss();
                        }

                        EncryptedSharedPreferencesManager encryptedSharedPreferencesManager = new EncryptedSharedPreferencesManager(requireContext());
                        encryptedSharedPreferencesManager.putString("name", fullName);
                        encryptedSharedPreferencesManager.putString("email", email);
                        encryptedSharedPreferencesManager.putString("mobile", phone);
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

                        FirebaseFirestore.getInstance().collection("Users").document(user.getUid())
                                .set(userData)
                                .addOnSuccessListener(aVoid -> navController.navigate(R.id.collectUserDetailsFragment))
                                .addOnFailureListener(e -> {
                                    CustomToast.showToast(requireActivity(), "Failed to save user data. Please try again.");
                                    createAccountButton.setEnabled(true);
                                    createAccountButton.setAlpha(1.0f);
                                });
                    } else {
                        if (otpVerificationDialog != null && otpVerificationDialog.isShowing()) {
                            verifyOtpButton.setEnabled(true);
                        } else {
                            createAccountButton.setEnabled(true);
                            createAccountButton.setAlpha(1.0f);
                        }

                        String errorMessage = "Account creation failed";
                        if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                            errorMessage = "Email already in use.";
                        } else {
                            errorMessage = "Account creation failed: " + task.getException().getMessage();
                        }
                        CustomToast.showToast(requireActivity(), errorMessage);
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
                .setMessage("Resend OTP to " + countryCode + phone + "?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    if (otpCountDownTimer != null) {
                        otpCountDownTimer.cancel();
                    }
                    sendOtp(countryCode + phone);
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