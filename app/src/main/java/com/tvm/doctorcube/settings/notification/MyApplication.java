package com.tvm.doctorcube.settings.notification;


import android.app.Application;
import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class MyApplication extends Application {
    private static final String WORK_NAME = "daily_notification";

    @Override
    public void onCreate() {
        super.onCreate();
        scheduleDailyNotification();
    }

    private void scheduleDailyNotification() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 9); // Default to 9:00 AM
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        long currentTime = System.currentTimeMillis();
        long notificationTime = calendar.getTimeInMillis();
        if (notificationTime < currentTime) {
            notificationTime += 24 * 60 * 60 * 1000; // Add one day if time has passed
        }

        long initialDelay = notificationTime - currentTime;

        PeriodicWorkRequest workRequest = new PeriodicWorkRequest.Builder(NotificationWorker.class, 24, TimeUnit.HOURS)
                .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
                .setConstraints(Constraints.NONE)
                .build();

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP, // Keep existing work if already scheduled
                workRequest
        );
    }
}
