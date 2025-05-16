package com.tvm.doctorcube.authentication;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import com.airbnb.lottie.LottieAnimationView;
import com.tvm.doctorcube.CustomToast;
import com.tvm.doctorcube.R;
import java.util.ArrayList;
import java.util.List;

public class FragmentAskUser extends Fragment {

    private static final int REQUEST_ALL_PERMISSIONS = 101;
    private TextView headingText;
    private LottieAnimationView lottieAnimationView;
    private final String[] headings = {"Build Your Career", "Grow Your Skills", "Shape Your Future"};
    private final int[] lottieFiles = {R.raw.anim_ask_user1, R.raw.anim_ask_user2, R.raw.anim_ask_user3};
    private int currentIndex = 0;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private static final long DELAY_MS = 3000; // 3 seconds delay between transitions
    private boolean isAnimating = false;
    private AppCompatButton loginButton;
    private AppCompatButton createAccountButton;
    private int navigationDestinationId = -1; // Store the navigation ID
    private boolean permissionsRequested = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_ask_user, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize UI elements
        headingText = view.findViewById(R.id.heading_text);
        lottieAnimationView = view.findViewById(R.id.lottieAnimationView);
        loginButton = view.findViewById(R.id.loginButton);
        createAccountButton = view.findViewById(R.id.createAccountButton);

        // Set initial state
        updateHeadingAndAnimation(false); // Pass false for no animation on initial load

        // Start the looping mechanism
        startAnimationLoop();

        // Request permissions when the fragment is created
        if (!permissionsRequested) {
            requestAllPermissions(view);
            permissionsRequested = true; // Ensure permissions are requested only once
        }

        // Set button click listeners
        loginButton.setOnClickListener(v -> {
            // Navigate only if permissions are granted
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                Navigation.findNavController(v).navigate(R.id.loginFragment2);
            } else {
                CustomToast.showToast(requireActivity(),"Please allow permissions to proceed");
            }

        });

        createAccountButton.setOnClickListener(v -> {
            // Navigate only if permissions are granted
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                Navigation.findNavController(v).navigate(R.id.createAccountFragment2);
            } else {
                CustomToast.showToast(requireActivity(),"Please allow permissions to proceed");
            }
        });
    }

    private void requestAllPermissions(View view) {
        List<String> permissionsToRequest = new ArrayList<>();

        // Check for each permission
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            permissionsToRequest.add(Manifest.permission.CALL_PHONE);
        }
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
            permissionsToRequest.add(Manifest.permission.INTERNET);
        }
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            permissionsToRequest.add(Manifest.permission.POST_NOTIFICATIONS);
        }
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P &&
                ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissionsToRequest.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S &&
                ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissionsToRequest.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }

        if (!permissionsToRequest.isEmpty()) {
            // Request the permissions
            ActivityCompat.requestPermissions(requireActivity(),
                    permissionsToRequest.toArray(new String[0]),
                    REQUEST_ALL_PERMISSIONS);
            // Store the destinationId, so we can navigate after permission is granted
        } else {
            // All permissions are already granted, navigate to the next fragment
            // Navigation.findNavController(getView()).navigate(navigationDestinationId);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_ALL_PERMISSIONS) {
            boolean allPermissionsGranted = true;
            if (grantResults.length > 0) {
                for (int grantResult : grantResults) {
                    if (grantResult != PackageManager.PERMISSION_GRANTED) {
                        allPermissionsGranted = false;
                        break;
                    }
                }
            } else {
                allPermissionsGranted = false; // Handle the case where grantResults is empty
            }

            if (allPermissionsGranted) {
                // All permissions were granted, navigate to the next fragment
                if (getView() != null ) {
                    // Navigation.findNavController(getView()).navigate(navigationDestinationId);
                    //navigationDestinationId = -1; // Reset, not needed here, set  on button clicks
                }
            } else {
                // Some or all permissions were denied, show a message
                CustomToast.showToast(requireActivity(),"Please allow permissions to proceed");
            }
        }
    }

    private void updateHeadingAndAnimation(boolean animate) {
        if (isAnimating) return; // Prevent concurrent animations

        final String newHeading = headings[currentIndex];
        final int newLottieFile = lottieFiles[currentIndex];

        if (animate) {
            isAnimating = true;
            // Fade out current heading and animation
            headingText.animate().alpha(0f).setDuration(500).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    headingText.setText(newHeading);
                    headingText.animate().alpha(1f).setDuration(500).setListener(null); //fade in
                }
            });

            lottieAnimationView.animate().alpha(0f).setDuration(500).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    // Change Lottie animation source and play
                    lottieAnimationView.setAnimation(newLottieFile);
                    lottieAnimationView.playAnimation();
                    lottieAnimationView.animate().alpha(1f).setDuration(500).setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            isAnimating = false; //allow next animation
                        }
                    }); // Fade in
                }
            });
        } else {
            // If not animating, set values directly
            headingText.setText(newHeading);
            lottieAnimationView.setAnimation(newLottieFile);
            lottieAnimationView.playAnimation();
            isAnimating = false;
        }
    }

    private void startAnimationLoop() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Move to the next index, loop back to 0 if at the end
                currentIndex = (currentIndex + 1) % headings.length;
                updateHeadingAndAnimation(true); // Pass true to animate
                // Schedule the next update
                handler.postDelayed(this, DELAY_MS);
            }
        }, DELAY_MS);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Clean up the handler to prevent memory leaks
        handler.removeCallbacksAndMessages(null);
    }
}
