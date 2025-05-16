package com.tvm.doctorcube.dialoguebox;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import com.tvm.doctorcube.R;
import com.tvm.doctorcube.CustomToast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

public class OtpVerificationDialogFragment extends DialogFragment {

    private static final String TAG = "OtpVerificationDialog";
    private static final long OTP_TIMEOUT_MS = 60000; // 60 seconds

    private EditText otpEditText;
    private Button verifyOtpButton;
    private Button cancelOtpButton;
    private TextView resendOtpText;
    private String verificationId;
    private OtpVerificationListener listener;
    private CountDownTimer otpCountDownTimer;
    private FirebaseAuth mAuth;
    private View dialogView; //keep a reference

    public interface OtpVerificationListener {
        void onOtpVerified(PhoneAuthCredential credential);
        void onVerificationFailed(String errorMessage);
        void onResendOtp();
    }

    public static OtpVerificationDialogFragment newInstance(String verificationId, String phoneNumber, OtpVerificationListener listener) {
        OtpVerificationDialogFragment fragment = new OtpVerificationDialogFragment();
        Bundle args = new Bundle();
        args.putString("verificationId", verificationId);
        args.putString("phoneNumber", phoneNumber); // Pass phone number for resend
        fragment.setArguments(args);
        fragment.setListener(listener);
        return fragment;
    }

    public void setListener(OtpVerificationListener listener) {
        this.listener = listener;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.CustomDialog); // Optional: Style
        mAuth = FirebaseAuth.getInstance();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        dialogView = inflater.inflate(R.layout.otp_verification_bottom_sheet, container, false); // Store the view
        verificationId = getArguments().getString("verificationId");
        String phoneNumber = getArguments().getString("phoneNumber");

        // Initialize views
        otpEditText = dialogView.findViewById(R.id.otpNumberVerification);
        verifyOtpButton = dialogView.findViewById(R.id.verifyOtpButton);
        cancelOtpButton = dialogView.findViewById(R.id.cancelOtpButton);
        resendOtpText = dialogView.findViewById(R.id.resendOtpText);

        // Set click listeners
        verifyOtpButton.setOnClickListener(v -> {
            String otp = otpEditText.getText().toString().trim();
            if (TextUtils.isEmpty(otp)) {
                otpEditText.setError("Enter OTP");
                shakeView(otpEditText);
                return;
            }
            if (listener != null) {
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, otp);
                listener.onOtpVerified(credential);
            }
        });

        cancelOtpButton.setOnClickListener(v -> {
            dismissWithAnimation();
            if (otpCountDownTimer != null) {
                otpCountDownTimer.cancel();
            }
        });

        resendOtpText.setOnClickListener(v -> {
            if (listener != null) {
                listener.onResendOtp();
            }
            if (otpCountDownTimer != null) {
                otpCountDownTimer.cancel();
            }
            startOtpCountdown();
        });

        startOtpCountdown();
        // Entry animation
        dialogView.setAlpha(0f);
        dialogView.setTranslationY(50);
        dialogView.animate()
                .alpha(1f)
                .translationY(0)
                .setDuration(300)
                .start();

        return dialogView;
    }

    private void startOtpCountdown() {
        resendOtpText.setEnabled(false);
        otpCountDownTimer = new CountDownTimer(OTP_TIMEOUT_MS, 1000) {
            public void onTick(long millisUntilFinished) {
                if (isAdded()) {
                    resendOtpText.setText("Resend in " + millisUntilFinished / 1000 + "s");
                }
            }

            public void onFinish() {
                if (isAdded()) {
                    resendOtpText.setText("Resend OTP");
                    resendOtpText.setEnabled(true);
                }
                if (otpCountDownTimer != null) {
                    otpCountDownTimer.cancel();
                }
            }
        }.start();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (otpCountDownTimer != null) {
            otpCountDownTimer.cancel();
        }
    }

    private void shakeView(View view) {
        Animation shake = AnimationUtils.loadAnimation(getContext(), R.anim.shake);
        view.startAnimation(shake);
    }
    private void dismissWithAnimation() {
        if (dialogView != null) {
            ViewPropertyAnimator animator = dialogView.animate()
                    .alpha(0f)
                    .translationY(50)
                    .setDuration(200);

            animator.setListener(new Animator.AnimatorListener() {  // Use Animator.AnimatorListener
                @Override
                public void onAnimationStart(Animator animation) {
                    // Optional:  Do something at the start of the animation
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    dismiss();
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                    // Optional:  Do something if the animation is canceled.
                }

                @Override
                public void onAnimationRepeat(Animator animation) {
                    // Optional:  Do something if the animation repeats.
                }
            });
            animator.start();
        } else {
            dismiss();
        }
    }
}

