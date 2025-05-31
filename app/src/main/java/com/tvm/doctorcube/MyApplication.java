package com.tvm.doctorcube;

import android.app.Application;
import android.util.Log;

import com.google.firebase.FirebaseApp;
import com.google.firebase.appcheck.FirebaseAppCheck;
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory;
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.messaging.FirebaseMessaging;

public class MyApplication extends Application {
    private static final String TAG = "MyApplication";

    @Override
    public void onCreate() {
        super.onCreate();

        // Initialize Firebase once
        FirebaseApp.initializeApp(this);

        // Enable Firebase Crashlytics
        FirebaseCrashlytics crashlytics = FirebaseCrashlytics.getInstance();
        crashlytics.setCrashlyticsCollectionEnabled(true);
        crashlytics.log("App started successfully!");

        // Enable Firebase Analytics
        FirebaseAnalytics.getInstance(this);

        // Configure Firebase App Check
        configureAppCheck();

        // Initialize FCM and retrieve token
        initializeFCM();

        // Set custom uncaught exception handler
        Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
            Log.e(TAG, "Uncaught exception", throwable);
            crashlytics.recordException(throwable);
            // Avoid System.exit(1) to allow background processes (e.g., FCM) to continue
            // Instead, let the system handle the crash or redirect to an error screen
        });
    }

    private void configureAppCheck() {
        FirebaseAppCheck firebaseAppCheck = FirebaseAppCheck.getInstance();
        if (BuildConfig.DEBUG) {
            // Use Debug provider for debug builds
            firebaseAppCheck.installAppCheckProviderFactory(DebugAppCheckProviderFactory.getInstance());
            Log.d(TAG, "App Check: Debug provider initialized");
        } else {
            // Use Play Integrity for release builds
            firebaseAppCheck.installAppCheckProviderFactory(PlayIntegrityAppCheckProviderFactory.getInstance());
            Log.d(TAG, "App Check: Play Integrity provider initialized");
        }
    }

    private void initializeFCM() {
        // Retrieve FCM token (optional, can also be done in messaging service)
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.w(TAG, "Fetching FCM token failed", task.getException());
                return;
            }
            String token = task.getResult();
            Log.d(TAG, "FCM Token: " + token);
            // Optionally send token to your server
            sendTokenToServer(token);
        });
    }

    private void sendTokenToServer(String token) {
        // TODO: Implement server-side logic to store token (e.g., in Firestore)
        Log.d(TAG, "Sending FCM token to server: " + token);
    }
}