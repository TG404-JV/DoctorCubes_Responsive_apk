package com.tvm.doctorcube.settings.notification;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import androidx.core.app.NotificationCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import com.tvm.doctorcube.MainActivity; // Replace with your target activity
import com.tvm.doctorcube.R;
import com.tvm.doctorcube.settings.notification.NotificationDatabase;

public class NotificationWorker extends Worker {
    private static final String CHANNEL_ID = "default_notifications";

    public NotificationWorker(Context context, WorkerParameters params) {
        super(context, params);
    }

    @Override
    public Result doWork() {
        createNotificationChannel();
        showNotification("Daily University Update", "Check your application status today!", System.currentTimeMillis());
        return Result.success();
    }

    private void createNotificationChannel() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationManager manager = getApplicationContext().getSystemService(NotificationManager.class);
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Default Notifications", NotificationManager.IMPORTANCE_DEFAULT);
            manager.createNotificationChannel(channel);
        }
    }

    private void showNotification(String title, String message, long timestamp) {
        NotificationManager manager = getApplicationContext().getSystemService(NotificationManager.class);

        // Create an intent to open the app
        Intent intent = new Intent(getApplicationContext(), MainActivity.class); // Replace MainActivity with your target activity
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        // Create a PendingIntent
        PendingIntent pendingIntent = PendingIntent.getActivity(
                getApplicationContext(),
                0, // Request code
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE // FLAG_IMMUTABLE required for Android 12+
        );

        // Build the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification) // Ensure this drawable exists
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true) // Dismiss notification on click
                .setContentIntent(pendingIntent); // Attach the PendingIntent

        // Show the notification
        manager.notify((int) timestamp, builder.build());

        // Save to database
        NotificationDatabase.getInstance(getApplicationContext()).saveNotification(title, message, timestamp);
    }
}