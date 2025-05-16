package com.tvm.doctorcube;

import android.app.Application;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.analytics.FirebaseAnalytics;


import com.google.firebase.FirebaseApp;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // Initialize Firebase
        FirebaseApp.initializeApp(this);

        // Enable Firebase Crashlytics
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true);

        // Enable Firebase Analytics
        FirebaseAnalytics.getInstance(this);

        // Log app start event
        FirebaseCrashlytics.getInstance().log("App started successfully!");

        // Capture Uncaught Exceptions Globally
        Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
            FirebaseCrashlytics.getInstance().recordException(throwable);
            System.exit(1);
        });
    }
}


