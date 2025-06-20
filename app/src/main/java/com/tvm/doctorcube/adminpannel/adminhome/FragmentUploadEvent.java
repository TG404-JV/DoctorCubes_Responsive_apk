package com.tvm.doctorcube.adminpannel.adminhome;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.tvm.doctorcube.R;
import com.tvm.doctorcube.databinding.FragmentUploadEventBinding;
import com.tvm.doctorcube.home.model.UpcomingEvent;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class FragmentUploadEvent extends Fragment {

    private FragmentUploadEventBinding binding;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private Calendar calendar;
    private Uri selectedImageUri;
    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private boolean isUploading = false;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container, @NonNull Bundle savedInstanceState) {
        // Inflate the layout using View Binding
        binding = FragmentUploadEventBinding.inflate(inflater, container, false);

        // Initialize Firebase
        databaseReference = FirebaseDatabase.getInstance().getReference("UpcomingEvents");
        storageReference = FirebaseStorage.getInstance().getReference("events");

        // Initialize calendar
        calendar = Calendar.getInstance();

        // Setup category spinner
        String[] categories = {"Webinars", "Counseling", "Admissions", "Workshops"};
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, categories);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerCategory.setAdapter(categoryAdapter);

        // Setup event type spinner
        String[] eventTypes = {"Featured", "This Month", "Upcoming"};
        ArrayAdapter<String> eventTypeAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, eventTypes);
        eventTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerEventType.setAdapter(eventTypeAdapter);

        // Setup image picker
        imagePickerLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                selectedImageUri = result.getData().getData();
                binding.ivImagePreview.setImageURI(selectedImageUri);
                binding.ivImagePreview.setVisibility(View.VISIBLE);
            }
        });

        // Setup date picker
        binding.etEventDate.setOnClickListener(v -> showDatePicker());

        // Setup time pickers
        binding.etStartTime.setOnClickListener(v -> showTimePicker(binding.etStartTime));
        binding.etEndTime.setOnClickListener(v -> showTimePicker(binding.etEndTime));

        // Image selection
        binding.btnSelectImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            imagePickerLauncher.launch(intent);
        });

        // Submit button
        binding.btnSubmit.setOnClickListener(v -> checkAdminAndUpload());

        return binding.getRoot();
    }

    private void showDatePicker() {
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(),
                (view, year1, month1, dayOfMonth) -> {
                    String date = String.format(Locale.getDefault(), "%04d-%02d-%02d", year1, month1 + 1, dayOfMonth);
                    binding.etEventDate.setText(date);
                }, year, month, day);
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000); // Prevent past dates
        datePickerDialog.show();
    }

    private void showTimePicker(@NonNull TextInputEditText editText) {
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        TimePickerDialog timePickerDialog = new TimePickerDialog(requireContext(),
                (view, hourOfDay, minute1) -> {
                    String time = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute1);
                    editText.setText(time);
                }, hour, minute, true);
        timePickerDialog.show();
    }

    private void checkAdminAndUpload() {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Toast.makeText(requireContext(), "Please log in to upload events", Toast.LENGTH_SHORT).show();
            return;
        }

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseDatabase.getInstance().getReference("Users").child(uid).child("role")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String role = snapshot.getValue(String.class);
                        if ("admin".equalsIgnoreCase(role) || "superadmin".equalsIgnoreCase(role)) {
                            uploadEvent();
                        } else {
                            Toast.makeText(requireContext(), "Only admins can upload events", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(requireContext(), "Error checking role: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void uploadEvent() {
        if (isUploading) return;
        isUploading = true;
        binding.btnSubmit.setEnabled(false);
        binding.btnSubmit.setText("Uploading...");

        // Get input values
        String title = binding.etEventTitle.getText() != null ? binding.etEventTitle.getText().toString().trim() : "";
        String date = binding.etEventDate.getText() != null ? binding.etEventDate.getText().toString().trim() : "";
        String startTime = binding.etStartTime.getText() != null ? binding.etStartTime.getText().toString().trim() : "";
        String endTime = binding.etEndTime.getText() != null ? binding.etEndTime.getText().toString().trim() : "";
        String location = binding.etEventLocation.getText() != null ? binding.etEventLocation.getText().toString().trim() : "";
        String category = binding.spinnerCategory.getSelectedItem().toString();
        String eventType = binding.spinnerEventType.getSelectedItem().toString();
        String attendees = binding.etAttendees.getText() != null ? binding.etAttendees.getText().toString().trim() : "";
        boolean isPremium = binding.cbPremium.isChecked();
        boolean isFeatured = eventType.equals("Featured");

        // Validate inputs
        if (!validateInputs(title, date, startTime, endTime, location, attendees)) {
            isUploading = false;
            binding.btnSubmit.setEnabled(true);
            binding.btnSubmit.setText("Submit");
            return;
        }

        // Format time range
        String time = startTime + " - " + endTime;

        // Upload image to Firebase Storage
        String imagePath = "event_" + UUID.randomUUID().toString() + ".jpg";
        StorageReference imageRef = storageReference.child(imagePath);
        imageRef.putFile(selectedImageUri)
                .continueWithTask(task -> {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return imageRef.getDownloadUrl();
                })
                .addOnSuccessListener(downloadUri -> {
                    // Create event object
                    String eventId = databaseReference.push().getKey();
                    if (eventId == null) {
                        Toast.makeText(requireContext(), "Failed to generate event ID", Toast.LENGTH_SHORT).show();
                        isUploading = false;
                        binding.btnSubmit.setEnabled(true);
                        binding.btnSubmit.setText("Submit");
                        return;
                    }

                    UpcomingEvent event = new UpcomingEvent(
                            eventId, title, category, date, time, location, attendees,
                            downloadUri.toString(), isPremium, isFeatured
                    );

                    // Determine Firebase path
                    String path = eventType.equals("Featured") ? "featured" :
                            (eventType.equals("This Month") || isThisMonthEvent(date)) ? "this_month" : "upcoming";

                    // Upload to Firebase Database
                    databaseReference.child(path).child(eventId).setValue(event)
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(requireContext(), "Event uploaded successfully", Toast.LENGTH_SHORT).show();
                                clearForm();
                                isUploading = false;
                                binding.btnSubmit.setEnabled(true);
                                binding.btnSubmit.setText("Submit");
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(requireContext(), "Failed to upload event: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                isUploading = false;
                                binding.btnSubmit.setEnabled(true);
                                binding.btnSubmit.setText("Submit");
                            });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(requireContext(), "Failed to upload image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    isUploading = false;
                    binding.btnSubmit.setEnabled(true);
                    binding.btnSubmit.setText("Submit");
                });
    }

    private boolean validateInputs(String title, String date, String startTime, String endTime, String location, String attendees) {
        if (title.isEmpty()) {
            binding.etEventTitle.setError("Title is required");
            return false;
        }
        if (date.isEmpty() || !isValidDate(date)) {
            binding.etEventDate.setError("Valid date is required (YYYY-MM-DD)");
            return false;
        }
        if (startTime.isEmpty() || !isValidTime(startTime)) {
            binding.etStartTime.setError("Valid start time is required (HH:MM)");
            return false;
        }
        if (endTime.isEmpty() || !isValidTime(endTime)) {
            binding.etEndTime.setError("Valid end time is required (HH:MM)");
            return false;
        }
        if (!isEndTimeAfterStartTime(startTime, endTime)) {
            binding.etEndTime.setError("End time must be after start time");
            return false;
        }
        if (location.isEmpty()) {
            binding.etEventLocation.setError("Location is required");
            return false;
        }
        if (attendees.isEmpty()) {
            binding.etAttendees.setError("Attendees information is required");
            return false;
        }
        try {
            int attendeesCount = Integer.parseInt(attendees);
            if (attendeesCount <= 0) {
                binding.etAttendees.setError("Attendees must be a positive number");
                return false;
            }
        } catch (NumberFormatException e) {
            binding.etAttendees.setError("Attendees must be a valid number");
            return false;
        }
        if (selectedImageUri == null) {
            Toast.makeText(requireContext(), "Please select an event image", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private boolean isValidDate(String date) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            sdf.setLenient(false);
            sdf.parse(date);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    private boolean isValidTime(String time) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
            sdf.setLenient(false);
            sdf.parse(time);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    private boolean isEndTimeAfterStartTime(String startTime, String endTime) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
            Date start = sdf.parse(startTime);
            Date end = sdf.parse(endTime);
            return end.after(start);
        } catch (ParseException e) {
            return false;
        }
    }

    private boolean isThisMonthEvent(String date) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date eventDate = sdf.parse(date);
            Calendar eventCal = Calendar.getInstance();
            eventCal.setTime(eventDate);

            Calendar currentCal = Calendar.getInstance();
            return eventCal.get(Calendar.YEAR) == currentCal.get(Calendar.YEAR) &&
                    eventCal.get(Calendar.MONTH) == currentCal.get(Calendar.MONTH);
        } catch (ParseException e) {
            return false;
        }
    }

    private void clearForm() {
        binding.etEventTitle.setText("");
        binding.etEventDate.setText("");
        binding.etStartTime.setText("");
        binding.etEndTime.setText("");
        binding.etEventLocation.setText("");
        binding.etAttendees.setText("");
        binding.cbPremium.setChecked(false);
        binding.spinnerCategory.setSelection(0);
        binding.spinnerEventType.setSelection(0);
        selectedImageUri = null;
        binding.ivImagePreview.setImageDrawable(null);
        binding.ivImagePreview.setVisibility(View.GONE);
        binding.etEventTitle.setError(null);
        binding.etEventDate.setError(null);
        binding.etStartTime.setError(null);
        binding.etEndTime.setError(null);
        binding.etEventLocation.setError(null);
        binding.etAttendees.setError(null);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Prevent memory leaks
    }
}