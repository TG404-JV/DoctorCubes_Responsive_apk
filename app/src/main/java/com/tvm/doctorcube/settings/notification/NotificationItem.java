package com.tvm.doctorcube.settings.notification;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

// Notification Entity
@Entity(tableName = "notifications")
public class NotificationItem {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String title;
    public String message;
    public long timestamp;

    public NotificationItem(String title, String message, long timestamp) {
        this.title = title;
        this.message = message;
        this.timestamp = timestamp;
    }

    // Getters (required for Room)
    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getMessage() { return message; }
    public long getTimestamp() { return timestamp; }
}

// DAO (Data Access Object)
@Dao
interface NotificationDao {
    @Insert
    void insert(NotificationItem notification);

    @Query("SELECT * FROM notifications ORDER BY timestamp DESC")
    List<NotificationItem> getAllNotifications();

    @Query("DELETE FROM notifications")
    void clearAll();
}

