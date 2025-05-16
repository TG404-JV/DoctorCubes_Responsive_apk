package com.tvm.doctorcube.adminpannel.databsemanager;



import android.util.Log;

import com.google.firebase.firestore.DocumentSnapshot;

import java.util.Date;

public class FirestoreDataHelper {
    private static final String TAG = "FirestoreDataHelper";

    public static String getString(DocumentSnapshot snapshot, String key) {
        return getString(snapshot, key, null);
    }

    public static String getString(DocumentSnapshot snapshot, String key, String defaultValue) {
        if (snapshot.contains(key)) {
            try {
                Object value = snapshot.get(key);
                if (value != null) {
                    return value.toString().trim();
                }
            } catch (Exception e) {
                Log.e(TAG, "Error getting string for key: " + key, e);
                return defaultValue;
            }
        }
        return defaultValue;
    }

    public static Boolean getBoolean(DocumentSnapshot snapshot, String key, boolean defaultValue) {
        if (snapshot.contains(key)) {
            try {
                Boolean value = snapshot.getBoolean(key);
                if (value != null) {
                    return value;
                }
            } catch (Exception e) {
                Log.e(TAG, "Error getting boolean for key: " + key, e);
                return defaultValue;
            }
        }
        return defaultValue;
    }

    public static Date getTimestamp(DocumentSnapshot snapshot, String key) {
        if (snapshot.contains(key)) {
            try {
                com.google.firebase.Timestamp timestamp = snapshot.getTimestamp(key);
                if (timestamp != null) {
                    return timestamp.toDate();
                }
            } catch (Exception e) {
                Log.e(TAG, "Error getting timestamp for key: " + key, e);
                return null;
            }
        }
        return null;
    }
}