package com.tvm.doctorcube.adminpannel.adminhome;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.tvm.doctorcube.R;
import com.tvm.doctorcube.home.model.UpcomingEvent;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class FragmentUploadEvent extends Fragment {

    private TextInputEditText etEventTitle, etEventDate, etStartTime, etEndTime, etEventLocation, etImageUrl;
    private Spinner spinnerCategory;
    private CheckBox cbPremium;
    private MaterialButton btnSubmit;
    private DatabaseReference databaseReference;
    private Calendar calendar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_upload_event, container, false);

        // Initialize views
        etEventTitle = view.findViewById(R.id.etEventTitle);
        etEventDate = view.findViewById(R.id.etEventDate);
        etStartTime = view.findViewById(R.id.etStartTime);
        etEndTime = view.findViewById(R.id.etEndTime);
        etEventLocation = view.findViewById(R.id.etEventLocation);
        etImageUrl = view.findViewById(R.id.etImageUrl);
        spinnerCategory = view.findViewById(R.id.spinnerCategory);
        cbPremium = view.findViewById(R.id.cbPremium);
        btnSubmit = view.findViewById(R.id.btnSubmit);

        // Initialize Firebase
        databaseReference = FirebaseDatabase.getInstance().getReference("events");

        // Initialize calendar
        calendar = Calendar.getInstance();

        // Setup category spinner
        String[] categories = {"Webinars", "Counseling", "Admissions", "Workshops"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);

        // Setup date picker
        etEventDate.setOnClickListener(v -> {
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(),
                    (view1, year1, month1, dayOfMonth) -> {
                        String date = String.format(Locale.getDefault(), "%04d-%02d-%02d", year1, month1 + 1, dayOfMonth);
                        etEventDate.setText(date);
                    }, year, month, day);
            datePickerDialog.show();
        });

        // Setup time pickers
        etStartTime.setOnClickListener(v -> {
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);
            TimePickerDialog timePickerDialog = new TimePickerDialog(requireContext(),
                    (view1, hourOfDay, minute1) -> {
                        String time = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute1);
                        etStartTime.setText(time);
                    }, hour, minute, true);
            timePickerDialog.show();
        });

        etEndTime.setOnClickListener(v -> {
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);
            TimePickerDialog timePickerDialog = new TimePickerDialog(requireContext(),
                    (view1, hourOfDay, minute1) -> {
                        String time = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute1);
                        etEndTime.setText(time);
                    }, hour, minute, true);
            timePickerDialog.show();
        });

        // Submit button
        btnSubmit.setOnClickListener(v -> uploadEvent());

        return view;
    }

    private void uploadEvent() {
        // Get input values
        String title = etEventTitle.getText() != null ? etEventTitle.getText().toString().trim() : "";
        String date = etEventDate.getText() != null ? etEventDate.getText().toString().trim() : "";
        String startTime = etStartTime.getText() != null ? etStartTime.getText().toString().trim() : "";
        String endTime = etEndTime.getText() != null ? etEndTime.getText().toString().trim() : "";
        String location = etEventLocation.getText() != null ? etEventLocation.getText().toString().trim() : "";
        String category = spinnerCategory.getSelectedItem().toString();
        String imageUrl = etImageUrl.getText() != null ? etImageUrl.getText().toString().trim() : "";
        boolean isPremium = cbPremium.isChecked();

        // Validate inputs
        if (title.isEmpty()) {
            etEventTitle.setError("Title is required");
            return;
        }
        if (date.isEmpty()) {
            etEventDate.setError("Date is required");
            return;
        }
        if (startTime.isEmpty()) {
            etStartTime.setError("Start time is required");
            return;
        }
        if (endTime.isEmpty()) {
            etEndTime.setError("End time is required");
            return;
        }
        if (location.isEmpty()) {
            etEventLocation.setError("Location is required");
            return;
        }

        // Format time range
        String time = startTime + "-" + endTime;

        // Create event object
        UpcomingEvent event = new UpcomingEvent(title, date, time, location, category, isPremium, false, imageUrl);

        // Determine Firebase path
        String path = isThisMonthEvent(date) ? "this_month" : "upcoming";
        String eventId = databaseReference.child(path).push().getKey();

        // Upload to Firebase
        if (eventId != null) {
            databaseReference.child(path).child(eventId).setValue(event)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(requireContext(), "Event uploaded successfully", Toast.LENGTH_SHORT).show();
                        clearForm();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(requireContext(), "Failed to upload event: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private boolean isThisMonthEvent(String date) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date eventDate = sdf.parse(date);
            Calendar eventCal = Calendar.getInstance();
            eventCal.setTime(eventDate);

            Calendar currentCal = Calendar.getInstance();
            currentCal.set(2025, Calendar.MAY, 1); // Current date is May 2025

            return eventCal.get(Calendar.YEAR) == currentCal.get(Calendar.YEAR) &&
                    eventCal.get(Calendar.MONTH) == currentCal.get(Calendar.MONTH);
        } catch (ParseException e) {
            return false;
        }
    }

    private void clearForm() {
        etEventTitle.setText("");
        etEventDate.setText("");
        etStartTime.setText("");
        etEndTime.setText("");
        etEventLocation.setText("");
        etImageUrl.setText("");
        cbPremium.setChecked(false);
        spinnerCategory.setSelection(0);
    }
}