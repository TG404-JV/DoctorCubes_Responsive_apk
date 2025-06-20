package com.tvm.doctorcube.authentication.datamanager;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import java.io.IOException;
import java.security.GeneralSecurityException;

/**
 * A utility class for managing encrypted data storage using EncryptedSharedPreferences.
 * This class provides methods to securely store, retrieve, and remove different types of data.
 */
public class EncryptedSharedPreferencesManager {

    private static final String PREF_NAME = "EncryptedAppPreferences";
    private SharedPreferences encryptedSharedPreferences;
    private SharedPreferences.Editor editor;
    private static EncryptedSharedPreferencesManager instance;

    /**
     * Private constructor to enforce singleton pattern
     * @param context The application context
     */
    public EncryptedSharedPreferencesManager(Context context) {
        try {
            // Create or retrieve the Master Key for encryption
            MasterKey masterKey = new MasterKey.Builder(context)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build();

            // Create the EncryptedSharedPreferences using the Master Key
            encryptedSharedPreferences = EncryptedSharedPreferences.create(
                    context,
                    PREF_NAME,
                    masterKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );

            editor = encryptedSharedPreferences.edit();
        } catch (GeneralSecurityException | IOException e) {
            // In a production app, you might want to handle this differently
            throw new RuntimeException("Error initializing EncryptedSharedPreferences", e);
        }
    }

    /**
     * Get singleton instance of EncryptedSharedPreferencesManager
     * @param context The application context
     * @return The EncryptedSharedPreferencesManager instance
     */
    public static synchronized EncryptedSharedPreferencesManager getInstance(Context context) {
        if (instance == null) {
            instance = new EncryptedSharedPreferencesManager(context.getApplicationContext());
        }
        return instance;
    }

    /**
     * Store a string value securely
     * @param key The key to store the value under
     * @param value The string value to store
     */
    public void putString(String key, String value) {
        editor.putString(key, value);
        editor.apply();
    }

    /**
     * Get a stored string value
     * @param key The key of the value to retrieve
     * @param defaultValue The default value to return if the key doesn't exist
     * @return The stored string value, or defaultValue if not found
     */
    public String getString(String key, String defaultValue) {
        return encryptedSharedPreferences.getString(key, defaultValue);
    }

    /**
     * Store an integer value securely
     * @param key The key to store the value under
     * @param value The integer value to store
     */
    public void putInt(String key, int value) {
        editor.putInt(key, value);
        editor.apply();
    }

    /**
     * Get a stored integer value
     * @param key The key of the value to retrieve
     * @param defaultValue The default value to return if the key doesn't exist
     * @return The stored integer value, or defaultValue if not found
     */
    public int getInt(String key, int defaultValue) {
        return encryptedSharedPreferences.getInt(key, defaultValue);
    }

    /**
     * Store a boolean value securely
     * @param key The key to store the value under
     * @param value The boolean value to store
     */
    public void putBoolean(String key, boolean value) {
        editor.putBoolean(key, value);
        editor.apply();
    }

    /**
     * Get a stored boolean value
     * @param key The key of the value to retrieve
     * @param defaultValue The default value to return if the key doesn't exist
     * @return The stored boolean value, or defaultValue if not found
     */
    public boolean getBoolean(String key, boolean defaultValue) {
        return encryptedSharedPreferences.getBoolean(key, defaultValue);
    }

    /**
     * Store a float value securely
     * @param key The key to store the value under
     * @param value The float value to store
     */
    public void putFloat(String key, float value) {
        editor.putFloat(key, value);
        editor.apply();
    }

    /**
     * Get a stored float value
     * @param key The key of the value to retrieve
     * @param defaultValue The default value to return if the key doesn't exist
     * @return The stored float value, or defaultValue if not found
     */
    public float getFloat(String key, float defaultValue) {
        return encryptedSharedPreferences.getFloat(key, defaultValue);
    }

    /**
     * Store a long value securely
     * @param key The key to store the value under
     * @param value The long value to store
     */
    public void putLong(String key, long value) {
        editor.putLong(key, value);
        editor.apply();
    }

    /**
     * Get a stored long value
     * @param key The key of the value to retrieve
     * @param defaultValue The default value to return if the key doesn't exist
     * @return The stored long value, or defaultValue if not found
     */
    public long getLong(String key, long defaultValue) {
        return encryptedSharedPreferences.getLong(key, defaultValue);
    }

    /**
     * Check if a key exists in EncryptedSharedPreferences
     * @param key The key to check
     * @return true if key exists, false otherwise
     */
    public boolean contains(String key) {
        return encryptedSharedPreferences.contains(key);
    }

    /**
     * Remove a value from EncryptedSharedPreferences
     * @param key The key of the value to remove
     */
    public void remove(String key) {
        editor.remove(key);
        editor.apply();
    }

    /**
     * Clear all values from EncryptedSharedPreferences
     */
    public void clear() {
        editor.clear();
        editor.apply();
    }
}
