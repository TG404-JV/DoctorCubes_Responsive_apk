package com.tvm.doctorcube.settings;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.tvm.doctorcube.CustomToast;
import com.tvm.doctorcube.R;
import com.tvm.doctorcube.settings.notification.NotificationAdapter;
import com.tvm.doctorcube.settings.notification.NotificationDatabase;
import com.tvm.doctorcube.settings.notification.NotificationItem;
import com.tvm.doctorcube.settings.notification.NotificationWorker;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class NotificationPref extends Fragment {
    private SwitchCompat switchNotifications, switchUniversity, switchScholarship, switchVisa, switchApplication, switchSupport;
    private MaterialButton btnNotificationTime, btnResetTime;
    private RecyclerView recyclerViewNotifications;
    private ImageButton backBtn;
    private Toolbar toolbar; // Added toolbar field
    private SharedPreferences prefs;
    private NotificationAdapter adapter;
    private static final String PREFS_NAME = "NotificationPrefs";
    private static final String KEY_NOTIFICATION_TIME = "notification_time";
    private static final String KEY_NOTIFICATIONS_ENABLED = "notifications_enabled";
    private static final String WORK_NAME = "daily_notification";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notification_pref, container, false);

        // Initialize SharedPreferences
        prefs = requireActivity().getSharedPreferences(PREFS_NAME, requireActivity().MODE_PRIVATE);

        // Hide MainActivity Toolbar
        hideMainActivityToolbar();

        // Initialize views with null checks
        switchNotifications = view.findViewById(R.id.switch_notifications);
        switchUniversity = view.findViewById(R.id.switch_university);
        switchScholarship = view.findViewById(R.id.switch_scholarship);
        switchVisa = view.findViewById(R.id.switch_visa);
        switchApplication = view.findViewById(R.id.switch_application);
        switchSupport = view.findViewById(R.id.switch_support);
        btnNotificationTime = view.findViewById(R.id.btn_notification_time);
        btnResetTime = view.findViewById(R.id.btn_reset_time);
        recyclerViewNotifications = view.findViewById(R.id.recyclerview_notifications);
        backBtn = view.findViewById(R.id.backBtn);

        // Setup toolbar
        if (toolbar != null) {
            toolbar.setTitle("Notification Preferences");
            toolbar.setNavigationOnClickListener(v -> navigateToHome());
        }

        if (switchNotifications == null || btnNotificationTime == null || recyclerViewNotifications == null) {
            CustomToast.showToast((Activity) requireContext(), "Error initializing views");
            return view;
        }

        // Set default time and switch state
        String savedTime = prefs.getString(KEY_NOTIFICATION_TIME, "09:00 AM");
        btnNotificationTime.setText(savedTime);
        boolean notificationsEnabled = prefs.getBoolean(KEY_NOTIFICATIONS_ENABLED, true);
        switchNotifications.setChecked(notificationsEnabled);
        enableNotificationSettings(notificationsEnabled);

        // Setup RecyclerView asynchronously
        setupRecyclerView();

        // Back Btn Listener
        backBtn.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(v);
            navController.navigate(R.id.action_notificationPref_to_settingsHome);
        });

        // Setup listeners
        btnNotificationTime.setOnClickListener(v -> showTimePicker());
        btnResetTime.setOnClickListener(v -> resetTime());
        switchNotifications.setOnCheckedChangeListener((buttonView, isChecked) -> {
            enableNotificationSettings(isChecked);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean(KEY_NOTIFICATIONS_ENABLED, isChecked);
            editor.apply();
            if (!isChecked) {
                WorkManager.getInstance(requireContext()).cancelUniqueWork(WORK_NAME);
            } else {
                rescheduleNotification();
            }
        });

        // Schedule notifications if enabled
        if (notificationsEnabled) {
            rescheduleNotification();
        }

        return view;
    }

    private void setupRecyclerView() {
        recyclerViewNotifications.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new NotificationAdapter(null);
        recyclerViewNotifications.setAdapter(adapter);
        new LoadNotificationsTask().execute();
    }

    private void showTimePicker() {
        MaterialTimePicker picker = new MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_12H)
                .setHour(9)
                .setMinute(0)
                .setTitleText("Select Notification Time")
                .build();

        picker.addOnPositiveButtonClickListener(v -> {
            String time = String.format(Locale.getDefault(), "%02d:%02d %s",
                    picker.getHour() % 12 == 0 ? 12 : picker.getHour() % 12,
                    picker.getMinute(),
                    picker.getHour() >= 12 ? "PM" : "AM");
            btnNotificationTime.setText(time);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(KEY_NOTIFICATION_TIME, time);
            editor.apply();
            rescheduleNotification();
        });

        picker.show(getChildFragmentManager(), "TIME_PICKER");
    }

    private void resetTime() {
        btnNotificationTime.setText("09:00 AM");
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_NOTIFICATION_TIME, "09:00 AM");
        editor.apply();
        rescheduleNotification();
    }

    private void rescheduleNotification() {
        if (!switchNotifications.isChecked()) return;

        String time = prefs.getString(KEY_NOTIFICATION_TIME, "09:00 AM");
        Calendar calendar = Calendar.getInstance();
        int hour = Integer.parseInt(time.substring(0, 2));
        int minute = Integer.parseInt(time.substring(3, 5));
        boolean isPM = time.endsWith("PM");

        if (isPM && hour != 12) hour += 12;
        if (!isPM && hour == 12) hour = 0;

        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);

        long currentTime = System.currentTimeMillis();
        long notificationTime = calendar.getTimeInMillis();
        if (notificationTime < currentTime) {
            notificationTime += 24 * 60 * 60 * 1000;
        }

        long initialDelay = notificationTime - currentTime;

        PeriodicWorkRequest workRequest = new PeriodicWorkRequest.Builder(NotificationWorker.class, 24, TimeUnit.HOURS)
                .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
                .build();

        WorkManager.getInstance(requireContext()).enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.REPLACE,
                workRequest
        );
    }

    private void enableNotificationSettings(boolean enabled) {
        if (switchUniversity != null) switchUniversity.setEnabled(enabled);
        if (switchScholarship != null) switchScholarship.setEnabled(enabled);
        if (switchVisa != null) switchVisa.setEnabled(enabled);
        if (switchApplication != null) switchApplication.setEnabled(enabled);
        if (switchSupport != null) switchSupport.setEnabled(enabled);
        btnNotificationTime.setEnabled(enabled);
        btnResetTime.setEnabled(enabled);
    }

    private void hideMainActivityToolbar() {
        if (getActivity() instanceof AppCompatActivity) {
            AppCompatActivity activity = (AppCompatActivity) getActivity();
            Toolbar mainToolbar = activity.findViewById(R.id.toolbar);
            if (mainToolbar != null) {
                mainToolbar.setVisibility(View.GONE);
            }
            if (activity.getSupportActionBar() != null) {
                activity.getSupportActionBar().hide();
            }
        }
    }

    private void showMainActivityToolbar() {
        if (getActivity() instanceof AppCompatActivity) {
            AppCompatActivity activity = (AppCompatActivity) getActivity();
            Toolbar mainToolbar = activity.findViewById(R.id.toolbar);
            if (mainToolbar != null) {
                mainToolbar.setVisibility(View.VISIBLE);
            }
            if (activity.getSupportActionBar() != null) {
                activity.getSupportActionBar().show();
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Show MainActivity Toolbar when leaving this Fragment
        showMainActivityToolbar();
    }

    private void navigateToHome() {
        NavController navController = Navigation.findNavController(requireView());
        navController.navigate(R.id.action_notificationPref_to_settingsHome);
    }

    private class LoadNotificationsTask extends AsyncTask<Void, Void, List<NotificationItem>> {
        @Override
        protected List<NotificationItem> doInBackground(Void... voids) {
            return NotificationDatabase.getInstance(requireContext()).getNotifications();
        }

        @Override
        protected void onPostExecute(List<NotificationItem> notifications) {
            if (adapter != null) {
                adapter.updateNotifications(notifications);
            }
        }
    }
}