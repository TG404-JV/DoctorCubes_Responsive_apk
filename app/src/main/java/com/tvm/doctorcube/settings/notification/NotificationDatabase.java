package com.tvm.doctorcube.settings.notification;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import java.util.List;

// Database
@Database(entities = {NotificationItem.class}, version = 1, exportSchema = false)
public abstract class NotificationDatabase extends RoomDatabase {
    public abstract NotificationDao notificationDao();

    private static volatile NotificationDatabase INSTANCE;

    public static NotificationDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (NotificationDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    NotificationDatabase.class, "notification_db")
                            .fallbackToDestructiveMigration() // Handle migrations simply for now
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    // Helper methods for easier access
    public void saveNotification(String title, String message, long timestamp) {
        new Thread(() -> notificationDao().insert(new NotificationItem(title, message, timestamp))).start();
    }

    public List<NotificationItem> getNotifications() {
        return notificationDao().getAllNotifications();
    }

    public void clearNotifications() {
        new Thread(() -> notificationDao().clearAll()).start();
    }
}
