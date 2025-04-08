package com.tvm.doctorcube.dialoguebox;


import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.tvm.doctorcube.R;

import java.util.Objects;


public class MBBSApplicationDialog extends Dialog {

    private final String universityName;
    private final String userId;
    private final OnDialogActionListener listener;

    public interface OnDialogActionListener {
        void onProceed(String universityName, String userId);
        void onCancel();
    }

    public MBBSApplicationDialog(@NonNull Context context, String universityName, String userId, OnDialogActionListener listener) {
        super(context);
        this.universityName = universityName;
        this.userId = userId;
        this.listener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_mbbs_application);

        // Set transparent background for rounded corners
        Objects.requireNonNull(getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        // Initialize views
        TextView titleTextView = findViewById(R.id.dialog_title);
        TextView messageTextView = findViewById(R.id.dialog_message);
        ImageView logoImageView = findViewById(R.id.dialog_logo);
        Button proceedButton = findViewById(R.id.btn_proceed);
        Button cancelButton = findViewById(R.id.btn_cancel);
        View dialogContainer = findViewById(R.id.dialog_container);

        // Set text
        titleTextView.setText("Already Applied");
        messageTextView.setText("You have already applied to " + universityName + ". Do you want to proceed?");

        // Set button click listeners
        proceedButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onProceed(universityName, userId);
            }
            animateDialogExit(dialogContainer);
        });

        cancelButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onCancel();
            }
            animateDialogExit(dialogContainer);
        });

        // Apply entrance animation
        animateDialogEntrance(dialogContainer, logoImageView);
    }

    private void animateDialogEntrance(View dialogContainer, ImageView logoImageView) {
        // Main dialog animation
        Animation scaleIn = AnimationUtils.loadAnimation(getContext(), R.anim.scale_in);
        dialogContainer.startAnimation(scaleIn);

        // Logo animation
        Animation rotateIn = AnimationUtils.loadAnimation(getContext(), R.anim.rotate_in);
        logoImageView.startAnimation(rotateIn);
    }

    private void animateDialogExit(View dialogContainer) {
        Animation scaleOut = AnimationUtils.loadAnimation(getContext(), R.anim.scale_out);
        scaleOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                dismiss();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
        dialogContainer.startAnimation(scaleOut);
    }

    public static void show(Context context, String universityName, String userId, OnDialogActionListener listener) {
        MBBSApplicationDialog dialog = new MBBSApplicationDialog(context, universityName, userId, listener);
        dialog.setCancelable(false);
        dialog.show();
    }
}

