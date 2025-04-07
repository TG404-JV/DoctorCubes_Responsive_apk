package com.tvm.doctorcube.authentication;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;


import com.tvm.doctorcube.R;
import com.tvm.doctorcube.authentication.datamanager.EncryptedSharedPreferencesManager;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.security.GeneralSecurityException;

public class SplashFragment extends Fragment {

    private SharedPreferences sharedPreferences;
    private ShapeableImageView ivLogo;
    private TextView tvAppName, tvSlogan;
    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;
    private  EncryptedSharedPreferencesManager encryptedSharedPreferencesManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_splash, container, false);

        // Initialize views
        initializeViews(view);

        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
         encryptedSharedPreferencesManager = new EncryptedSharedPreferencesManager(requireActivity());



        // Check login status and navigate after 2 seconds
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Boolean isLogin = encryptedSharedPreferencesManager.getBoolean("isLogin", false);
            String role = encryptedSharedPreferencesManager.getString("role", null);
            if(isLogin)
            {
                if (role != null) {
                    if ("User".equals(role)) {
                        navController.navigate(R.id.mainActivity2);
                    } else if ("admin".equals(role) || "superadmin".equals(role)) {
                        navController.navigate(R.id.adminActivity2);
                    } else {
                        // Handle unknown roles
                        Log.w("SplashFragment", "Unknown user role: " + role);

                    }
                }
            }else {
                navigateToGetStarted(navController);

            }
        }, 2000);

        return view;
    }

    private void initializeViews(View view) {
        ivLogo = view.findViewById(R.id.splash_logo);
        tvAppName = view.findViewById(R.id.appname);
        tvSlogan = view.findViewById(R.id.slogun);
    }


    private void navigateToGetStarted(NavController navController) {
        mAuth.signOut();
        encryptedSharedPreferencesManager.clear();
        navController.navigate(R.id.getStartedFragment);
    }

}
